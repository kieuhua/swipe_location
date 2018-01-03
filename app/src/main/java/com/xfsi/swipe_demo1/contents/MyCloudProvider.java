package com.xfsi.swipe_demo1.contents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.webkit.MimeTypeMap;

import com.xfsi.swipe_demo1.R;
import com.xfsi.swipe_demo1.common.logger.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by local-kieu on 3/18/16.
 */
public class MyCloudProvider extends DocumentsProvider {
    private static final String TAG = "MyCloudProvider";

    private static final String[] DEFAULT_ROOT_PROJECTION = new String[]{
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
    };

    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            DocumentsContract.Document.COLUMN_FLAGS,
            DocumentsContract.Document.COLUMN_SIZE
    };
    // No official policy on how many to return, but make sure you do limit the number of recent
    // and search results.
    private static final int MAX_SEARCH_RESULTS =20;
    private static final int MAX_LAST_MODIFIED = 5;

    //k private static final String ROOT = "root";
    private static final String ROOT = "MyCloud";

    // A file object at the root of the file hierarchy.  Depending on your implementation, the root
    // does not need to be an existing file system directory.  For example, a tag-based document
    // provider might return a directory containing all tags, represented as child directories.
    private File mBaseDir;

    @Override public boolean onCreate() {
        Log.v(TAG, "onCreate");
        mBaseDir = getContext().getFilesDir();
        writeDummyFilesToStorage();
        return true;
    }

    /* Create a cursor with either the requested fields, or the default projection.  This
     cursor is returned to the Android system picker UI and used to display all roots from
     this provider.
        nologin => mcr empty; create row => mcr
        root_summary,flags=create,recent,research,title=appn,docId<=mBasedir,types<=childTypes(mBaseDir),
        space<=getFreespace,Icon<=ic_launcher
    */
    @Override public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        Log.v(TAG, "queryRoots");

        // Create a cursor with either the requested fields, or the default projection.  This
        // cursor is returned to the Android system picker UI and used to display all roots from
        // this provider.
        MatrixCursor mcr = new MatrixCursor(resolveRootProjection(projection));

        // If user is not logged in, return an empty root cursor.  This removes our provider from
        // the list entirely.
        if (!isUserLoggedIn()) {
            return mcr;
        }

        // It's possible to have multiple roots (e.g. for multiple accounts in the same app) -
        // just add multiple cursor rows.
        // Construct one row for a root called "MyCloud".
        MatrixCursor.RowBuilder row = mcr.newRow();

        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT);
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, getContext().getString(R.string.root_summary));

        // FLAG_SUPPORTS_CREATE means at least one directory under the root supports creating
        // documents.  FLAG_SUPPORTS_RECENTS means your application's most recently used
        // documents will show up in the "Recents" category.  FLAG_SUPPORTS_SEARCH allows users
        // to search all documents the application shares.
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_CREATE |
                DocumentsContract.Root.FLAG_SUPPORTS_RECENTS |
                DocumentsContract.Root.FLAG_SUPPORTS_SEARCH);

        // COLUMN_TITLE is the root title (e.g. what will be displayed to identify your provider).
        row.add(DocumentsContract.Root.COLUMN_TITLE, getContext().getString(R.string.app_name));

        // This document id must be unique within this provider and consistent across time.  The
        // system picker UI may save it and refer to it later.
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, getDocIdForFile(mBaseDir));

        // The child MIME types are used to filter the roots and only present to the user roots
        // that contain the desired type somewhere in their file hierarchy.
        row.add(DocumentsContract.Root.COLUMN_MIME_TYPES, getChildMimeType(mBaseDir));
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, mBaseDir.getFreeSpace());
        row.add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_launcher);

        return mcr;
    }

    /* just local filesytem for now
    rootId=>file,
    PriorityQueue(PQ) with lastModify comparator, add each to PQ,
    with dir add all files to pending linkedlist(LL) first, and while on pending LL
    includeFile to mcr
     */
    @Override public Cursor queryRecentDocuments(String rootId, String[] projection)
            throws FileNotFoundException {
        Log.v(TAG, "queryRecentDocuments");

        // This example implementation walks a local file structure to find the most recently
        // modified files.  Other implementations might include making a network call to query a
        // server.
        final File parent = getFileForDocId(rootId);

        // Create a queue to store the most recent documents, which orders by last modified.
        PriorityQueue<File> pQueue = new PriorityQueue<File>(5, new Comparator<File>() {
            public int compare(File i, File j) {
                return Long.compare(i.lastModified(), j.lastModified());
            }
        });
        final MatrixCursor mcr = new MatrixCursor(resolveDocumentProjection(projection));
        // Iterate through all files and directories in the file structure under the root.  If
        // the file is more recent than the least recently modified, add it to the queue,
        // limiting the number of results.
        final LinkedList<File> pending = new LinkedList<File>();

        pending.add(parent);

        // Do while we still have unexamined files
        while (!pending.isEmpty()) {
            // Take a file from the list of unprocessed files
            final File f = pending.removeFirst();
            if (f.isDirectory()) {
                // If it's a directory, add all its children to the unprocessed list
                Collections.addAll(pending, f.listFiles());
            } else {
                // If it's a file, add it to the ordered queue.
                pQueue.add(f);
            }
        }
        // Add the most recent files to the cursor, not exceeding the max number of results.
        for( int i=0; i < Math.min(MAX_LAST_MODIFIED + 1, pQueue.size()); i++) {
            final File f = pQueue.remove();       // AbstractQueue class
            includeFile(mcr, null, f);
        }

        return mcr;
    }


    @Override public Cursor querySearchDocuments(String rootId, String query, String[] projection)
                throws FileNotFoundException {
        Log.v(TAG, "querySearchDocuments");

        final File parent = getFileForDocId(rootId);
        final LinkedList<File> files = new LinkedList<File>();
        final MatrixCursor mcr = new MatrixCursor(resolveDocumentProjection(projection));

        // This example implementation searches file names for the query and doesn't rank search
        // results, so we can stop as soon as we find a sufficient number of matches.  Other
        // implementations might use other data about files, rather than the file name, to
        // produce a match; it might also require a network call to query a remote server.

        // Iterate through all files in the file structure under the root until we reach the
        // desired number of matches.

        files.add(parent);
        while (!files.isEmpty() && mcr.getCount() < MAX_SEARCH_RESULTS) {
            final File f = files.remove();
            if (f.isDirectory()) {
                Collections.addAll(files, f.listFiles());
            } else {
                if (f.getName().toLowerCase().contains(query)) {
                    includeFile(mcr, null, f);
                }
            }
        }
        return mcr;
    }

    // docId=>f => ParcelFileDescriptor => AssetFileDescriptor
    @Override public AssetFileDescriptor openDocumentThumbnail(String docId, Point sizeHint, CancellationSignal signal)
                throws FileNotFoundException {
        Log.v(TAG, "openDocumentThumbnail");

        final File f = getFileForDocId(docId);
        ParcelFileDescriptor pf = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY );
        return new AssetFileDescriptor(pf, 0, AssetFileDescriptor.UNKNOWN_LENGTH );
    }

    @Override public Cursor queryDocument(String docId, String[] projection)
            throws FileNotFoundException {
        final MatrixCursor mcr = new MatrixCursor(resolveDocumentProjection(projection));
        includeFile(mcr, docId, null);
        return mcr;
    }

    @Override public Cursor queryChildDocuments(String docId, String[] projection, String sort)
            throws FileNotFoundException {
        Log.v(TAG, "queryChildDocuments, parentDocumentId: " + docId + " sortOrder: " + sort);

        final File parent = getFileForDocId(docId);
        final MatrixCursor mcr = new MatrixCursor(resolveDocumentProjection(projection));

        for (File f : parent.listFiles()) {
            includeFile(mcr, null, f);
        }
        return mcr;
    }

    // if mode is 'w' => need handler, OnCloseListener; ParcelFileDescriptor(file,mode,[handler,listener])
    @Override public ParcelFileDescriptor openDocument(final String docId, final String mode, CancellationSignal signal)
                throws FileNotFoundException {
        Log.v(TAG, "openDocument, mode: " + mode);

        // It's OK to do network operations in this method to download the document, as long as you
        // periodically check the CancellationSignal.  If you have an extremely large file to
        // transfer from the network, a better solution may be pipes or sockets
        // (see ParcelFileDescriptor for helper methods).

        final File f = getFileForDocId(docId);
        final int modeInt = ParcelFileDescriptor.parseMode(mode);

        boolean isWrite = mode.indexOf('w') != -1;
        if (isWrite) {
            try {
                // Attach a close listener if the document is opened in write mode.
                final Handler h = new Handler(getContext().getMainLooper());
                return ParcelFileDescriptor.open(f, modeInt, h, new ParcelFileDescriptor.OnCloseListener() {
                    @Override
                    public void onClose(IOException e) {
                        // Update the file with the cloud server.  The client is done writing.
                        Log.i(TAG, "A file with id " + docId + " has been closed! Time to update the server");
                    }
                });
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open document with id " + docId + " and mode " + mode);
            }
        } else {
            return ParcelFileDescriptor.open(f, modeInt);
        }
    }

    @Override public String createDocument(String docId, String mimeType, String displayName)
        throws FileNotFoundException {
        Log.v(TAG, "createDocument");

        File parent = getFileForDocId(docId);
        File f = new File(parent.getPath(), displayName);
        try {
            f.createNewFile();
            f.setWritable(true);
            f.setReadable(true);
        } catch (IOException e) {
            throw new FileNotFoundException("Fail to create document with name " + displayName +
                    " and documentId " + docId);
        }
        return  getDocIdForFile(f);
    }

    @Override public void deleteDocument(String docId) throws FileNotFoundException {
        Log.v(TAG, "deleteDocument");

        final File f = getFileForDocId(docId);
        if (f.delete()) {
            Log.i(TAG, "Deleted file with id " + docId);
        } else {
            throw new FileNotFoundException("Failed to delete document with id: " + docId);
        }
    }

    // docId => f => mimeType
    @Override public String getDocumentType(String docId) throws FileNotFoundException {
        File f = getFileForDocId(docId);
        return getTypeForFile(f);
    }

    private static String[] resolveRootProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
    }

    private static String[] resolveDocumentProjection(String[] projection) {
       return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
    }

    // Add a representation of a file to a cursor.
    private void includeFile(MatrixCursor mcr, String docId, File f) throws FileNotFoundException {
        if ( null == docId ) {
            docId = getDocIdForFile(f);
        } else {
            f = getFileForDocId(docId);
        }
        int flags = 0;
        if (f.isDirectory()) {
            // Request the folder to lay out as a grid rather than a list. This also allows a larger
            // thumbnail to be displayed for each image.
            //            flags |= Document.FLAG_DIR_PREFERS_GRID;

            // Add FLAG_DIR_SUPPORTS_CREATE if the file is a writable directory.
            if (f.canWrite()) {
                flags |= DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE;
            } else if (f.canWrite()) {
                flags |= DocumentsContract.Document.FLAG_SUPPORTS_WRITE;
                flags |= DocumentsContract.Document.FLAG_SUPPORTS_DELETE;
            }
        }
        final String displayN = f.getName();
        final String mimeType = getTypeForFile(f);
        if (mimeType.startsWith("image/")) {
            // Allow the image to be represented by a thumbnail rather than an icon
            flags|= DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
        }

        final MatrixCursor.RowBuilder row = mcr.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, docId);
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, displayN);
        row.add(DocumentsContract.Document.COLUMN_SIZE, f.length());
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType);
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, f.lastModified());
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags);

        // Add a custom icon, R.drawable.ic_launcher is not in system.
        row.add(DocumentsContract.Document.COLUMN_ICON, R.drawable.ic_launcher);
    }

    private String getTypeForFile(File f) {
        if (f.isDirectory()) {
            return DocumentsContract.Document.MIME_TYPE_DIR;
        } else {
            final String name = f.getName();
            return getTypeForName(name);
        }
    }

    /* Get the MIME data type of a document, given its filename.
        last '.', substring => extension, mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
     */
    private String getTypeForName(String name) {
        final int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            final String ext = name.substring(lastDot + 1);
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    // HashSet, add (image, txt, docx), StringBuilder, append each + '\n' into String
    private String getChildMimeType(File parent) {
        final Set<String> types = new HashSet<String>();
        types.add("image/*");
        types.add("text/*");
        types.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        // Flatten the list into a string and insert newlines between the MIME type strings.
        final StringBuilder buf = new StringBuilder();
        for (String t : types) {
            buf.append(t).append("\n");
        }
        return buf.toString();
    }

    /**
     * Get the document ID given a File.  The document id must be consistent across time.  Other
     * applications may save the ID and use it to reference documents later.
     * <p/>
     * This implementation is specific to this demo.  It assumes only one root and is built
     * directly from the file structure.  However, it is possible for a document to be a child of
     * multiple directories (for example "android" and "images"), in which case the file must have
     * the same consistent, unique document ID in both cases.
     */
     private String getDocIdForFile(File f) {
         String path = f.getAbsolutePath();

         // Start at first char of path under root
         final String rootPath = mBaseDir.getPath();
         if (rootPath.equals(path)) {
             path = "";
         } else if (rootPath.endsWith("/")) {
             path = path.substring(rootPath.length());
         } else {
             path = path.substring(rootPath.length() + 1);
         }
         return  "root" + ":" + path;
     }

    // Translate your custom URI scheme into a File object.
    private File getFileForDocId(String docId) throws FileNotFoundException {
        File target = mBaseDir;
        if (docId.equals(ROOT)){
            return target;
        }
        final int splitIdx = docId.indexOf(':', 1);
        if ( splitIdx < 0) {
            throw new FileNotFoundException("Missing root for " + docId);
        } else {
            final String path = docId.substring(splitIdx +1);
            target = new File(target, path);
            if (!target.exists()) {
                throw new FileNotFoundException("Missing file for " + docId + "at " + target);
            }
            return target;
        }
    }

    /**
     * Preload sample files packaged in the apk into the internal storage directory.  This is a
     * dummy function specific to this demo.  The MyCloud mock cloud service doesn't actually
     * have a backend, so it simulates by reading content from the device's internal storage.
     */
    private void writeDummyFilesToStorage() {
        if (mBaseDir.list().length > 0) {
            return;     // files are already there.
        }
        int[] images = getResourceIdArray(R.array.image_res_ids);
        for (int resId : images) {
            writeFileToInternalStorage(resId, ".jpeg");
        }
        int[] texts = getResourceIdArray(R.array.text_res_ids);
        for (int resId : texts) {
            writeFileToInternalStorage(R.array.text_res_ids, ".txt");
        }
        int[] docxs = getResourceIdArray(R.array.docx_res_ids);
        for (int resId : docxs) {
            writeFileToInternalStorage(R.array.docx_res_ids, "docx");
        }

    }

    // Write a file to internal storage.  Used to set up our dummy "cloud server".
    private void writeFileToInternalStorage(int resId, String extension) {
        InputStream ins = getContext().getResources().openRawResource(resId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size;
        byte[] buf = new byte[1024];
        try {
            while ((size = ins.read(buf, 0, 1024)) >= 0) {
                baos.write(buf, 0, size);
            }
            ins.close();
            buf = baos.toByteArray();
            String fname = getContext().getResources().getResourceEntryName(resId) + extension;
            FileOutputStream fos = getContext().openFileOutput(fname, Context.MODE_PRIVATE);
            fos.write(buf);
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private int[] getResourceIdArray(int arrayResId) {
        TypedArray ar = getContext().getResources().obtainTypedArray(arrayResId);
        int len = ar.length();
        int[] resIds = new int[len];
        for(int i=0; i < len; i++){
            resIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        return resIds;
    }

    private boolean isUserLoggedIn() {
        final SharedPreferences sp = getContext().getSharedPreferences(getContext().getString(R.string.app_name),
                                        getContext().MODE_PRIVATE);
        return sp.getBoolean(getContext().getString(R.string.key_logged_in), false);
    }

}
