 /**
~  * Created by Tilak Acharya  on 13/09/21
~  * Contact : tilakacharya903@gmail.com
~  * Github : https://github.com/Tilak777
~  * Copyright (c) 2022 . All rights reserved.
~  * Last modified 3/10/22, 5:48 PM
~  */


package mad4124.team_sundry.task.ui.taskDetail

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import timber.log.Timber
import java.io.*
import kotlin.math.min

fun Uri.getPathFor(context: Context,myAppFileProvider:String):String?{
    Timber.d("""
        getPathFor
        has authority: ${this.authority}
        has scheme: ${this.scheme}
        has path: ${this.path}
        has lastPath segment: ${this.lastPathSegment}
        has userInfo: ${this.userInfo}
        has userInfo: ${this.userInfo}
        """)

    var path :String?= ""
    if(DocumentsContract.isDocumentUri(context,this)){
        Timber.d("is document URI")
        if(isLocalStorageDocument(myAppFileProvider)){
            path = DocumentsContract.getDocumentId(this)
            Timber.d("isLocalStorageDocument path:$path")
            return path
        }
        else if(isExternalStorageDocument()){
            Timber.d("isExternalStorageDocument")

            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":")
            val type = split[0]

            if("primary".equals(type,ignoreCase = true)){
                Timber.d("isExternalStorageDocument primary")
                path = ""+ Environment.getExternalStorageDirectory()+"/"+split[1]
//                path = ""+ context.externalCacheDir?.absolutePath+"/"+split[1]
                Timber.d("isExternalStorageDocument primary path : $path")
                return path
            }
            else if("secondary".equals(type,ignoreCase = true)){
                path = ""+Environment.getExternalStorageDirectory()+"/xxxx/" + split[1]
                Timber.d("isExternalStorageDocument home path : $path")
                return path
            }
            else if("home".equals(type,ignoreCase = true)){
                path = ""+Environment.getExternalStorageDirectory()+"/documents/" + split[1]
                Timber.d("isExternalStorageDocument home path : $path")
                return path
            }
            else if(scheme.equals("content",ignoreCase = true)){
//                path = getDataColumn(context,null,null)
//                path = getDataColumnTypeName(context,null,null)
//                path = getDataColumnTypeData(context,null,null)
                path = getSavedFilePathFromURI(context)
                Timber.d("isExternalStorageDocument scheme as content path : $path")
                return path
            }
            else{

//                val uri = Uri.parse(this.path)
                val file = File(this.path!!)
                Timber.d(""" self path: ${this.path}
                    new uri path : 
                    new file path: ${file.path}
                """.trimIndent())
                if(file.exists()){
                    return file.path
                }


                for(item in split){
                    Timber.d("split value $item")
                }
                val Apath = ""+Environment.getExternalStorageDirectory()
                Timber.d("""${Environment.getExternalStorageDirectory()}
                    ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}
                    ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}
                    ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}
                    storage state is ${Environment.getExternalStorageState()}
                    ${context.getExternalFilesDir(split[0]).toString()}
                    ${context.getExternalFilesDir(split[1]).toString()}
                        """)
//                ${ContextCompat.getExternalFilesDirs(context,split[0])} no work
//                ${ContextCompat.getExternalFilesDirs(context,split[1])} no work
//                ${System.getenv("SECONDARY_STORAGE")} no work

                path = Apath+"/"+split[1]
//                path = "/storage"+"/"+docId.replace(":","/")
                Timber.d("isExternalStorageDocument else path:$path")
                return path

//                Timber.d("isExternalStorageDocument else")
//                val DIR_SEPORATOR: Pattern = Pattern.compile("/")
//                val rv: Set<String> = HashSet()
//                val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")
//                val rawSecondaryStorageStr = System.getenv("SECONDARY_STORAGE")
//                val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")

//                if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
//                    if (TextUtils.isEmpty(rawExternalStorage)) {
//                        Timber.d("added /storage/sdcard0")
//                        rv.plus("/storage/sdcard0")
////                        rv.add("/storage/sdcard0")
//                    } else {
//                        Timber.d("added $rawExternalStorage")
//                        rv.plus(rawExternalStorage)
////                        rv.add(rawExternalStorage)
//                    }
//                }
//                else {
//                    val rawUserId:String
//                    val paths = Environment.getExternalStorageDirectory().absolutePath;
//                    Timber.d("absolute $paths")
//                    val folders = DIR_SEPORATOR.split(paths)
//                    val lastFolder = folders[folders.size - 1]
//                    var isDigit = false
//                    try {
//                        Integer.valueOf(lastFolder);
//                        isDigit = true
//                    }
//                    catch (ignored:NumberFormatException){
//                    }
//                    rawUserId = if(isDigit) lastFolder else ""
//
//                    if (TextUtils.isEmpty(rawUserId)) {
//                        Timber.d("added $rawEmulatedStorageTarget")
//                        rv.plus(rawEmulatedStorageTarget);
////                        rv.add(rawEmulatedStorageTarget);
//                    } else {
//                        val adder = rawEmulatedStorageTarget + File.separator + rawUserId
//                        Timber.d("added $adder")
//                        rv.plus(adder);
////                        rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
//                    }
//                }
//
//                if (!TextUtils.isEmpty(rawSecondaryStorageStr)) {
//                    if(rawSecondaryStorageStr!=null){
//                        val rawSecondaryStorages = rawSecondaryStorageStr.split(File.pathSeparator)
//                        val iterator = rawSecondaryStorages.iterator()
//                        while(iterator.hasNext()){
//                            val toAdd = iterator.next()
//                            Timber.d("adder rawSecondaryStorageStr $toAdd")
//                            rv.plus(toAdd)
//                        }
//                    }
//                }
//
//                val iterator = rv.iterator()
//                while(iterator.hasNext()){
//                    val fileName = iterator.next()
//                    val tempF = File(fileName+"/"+split[1])
//                    if(tempF.exists()){
//                        path = fileName+"/"+split[1]
//                        Timber.d("rv value $path")
//                    }
//                }
//
//                if(path==null){
//                    val fileName = this.getDataColumnType2(context,null,null)
//                    if (fileName != null) {
//                        path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
//                        Timber.d("fileName not null so got path:$path")
//                        return path
//                    }
//                }
//
//                Timber.d("isExternalStorageDocument primary else path:$path")
//                return path

            }
        }
        else if(isDownloadDocument()){
            Timber.d("isDownloadDocument")
            val fileName = this.getDataColumnTypeName(context,null,null)
            if (fileName != null) {
                path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                Timber.d("fileName not null so got path:$path")
                return path
            }

            var docId = DocumentsContract.getDocumentId(this)
            if(docId!=null && docId.startsWith("raw:")){
//                docId = docId.replaceFirst("raw:","")
//                val file = File(docId)
//                if(file.exists()){
//                    Timber.d("docId as path :$docId")
//                    return docId
//                }
                val subString = docId.substring(4)
                Timber.d("docId substring is $subString")
                return subString
            }

            val tryPrefixes = arrayListOf("content://downloads/public_downloads",
            "content://downloads/my_downloads",
            "content://downloads/all_downloads"
            )
            for(prefix in tryPrefixes){
                val contentURI = ContentUris.withAppendedId(Uri.parse(prefix),docId.toLong())
                try{
//                    if(prefix == tryPrefixes[0]){
//                        path = contentURI.getDataColumnType2(context,null,null)
//                    }
//                    else{
                        path = contentURI.getDataColumn(context,null,null)
//                    }

                    if(path!=null){
                        Timber.d("isDownloadDocument $prefix got path:$path")
                        return path
                    }
                    else{
                        Timber.d("isDownloadDocument $prefix got path: null")
                    }
                }
                catch(e:Exception){
                    Timber.d("prefix: $prefix \n error:${e.message} \n stackTrace:${e.stackTrace.toString()}")
                }
            }

            val fileNameA = getFileName(context,this,myAppFileProvider)
            Timber.d("got fileNameA : $fileNameA")
            val cacheDir = getDocumentCacheDir(context)
            val file = generateFileName(fileNameA, cacheDir)
            val destinationPath:String?
            if (file != null) {
                destinationPath = file.absolutePath;
                saveFileFromUri(context, this, destinationPath)
                Timber.d("save file from uri with path :$destinationPath")
                return destinationPath
            }

        }
        else if(isMediaDocument()){
            Timber.d("isMediaDocument")
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":")
            val type = split[0]
            var contentUri:Uri?=null
            when(type){
                "image" -> {contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI}
                "video" -> {contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI}
                "audio" -> { contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            path = contentUri?.getDataColumn(context,selection,selectionArgs)
            Timber.d("isMediaDocument path:$path")
            return path
        }
        else if(isGooglePhotosUri()){
            path = lastPathSegment
            Timber.d("isGooglePhotosUri path:$path")
            return path
        }
        else if(isGooglePhotosProviderUri()){
            val pathUri = this.path
            val newUri = pathUri!!.substring(pathUri.indexOf("content"),pathUri.lastIndexOf("/ACTUAL"))
            path = Uri.parse(newUri).getDataColumn(context,null,null)
            Timber.d("isGooglePhotosProviderUri path:$path")
            return path
        }
        else if(isGoogleDriveUri()){
            path = getSavedFilePathFromURI(context)
            Timber.d("isGoogleDriveUri path:$path")
            return path
        }
        else if(scheme.equals("content",ignoreCase = true)){
            Timber.d("scheme is $scheme")
            path = if(isGooglePhotosUri()){
                Timber.d("isGooglePhotosUri")
                lastPathSegment
            }
            else if(isGooglePhotosProviderUri()){
                val pathUri = this.path
                val newUri = pathUri!!.substring(pathUri.indexOf("content"),pathUri.lastIndexOf("/ACTUAL"))
                Timber.d("isGooglePhotosProviderUri path:$path")
                Uri.parse(newUri).getDataColumn(context,null,null)
            }
            else if(isGoogleDriveUri()){
                Timber.d("isGoogleDriveUri")
                getSavedFilePathFromURI(context)
            }
            else if(isOneDriveUri()){
                Timber.d("isOneDriveUri")
                getSavedFilePathFromURI(context)
            }else{
                Timber.d("content scheme none")
//                getDataColumn(context,null,null)
                getDataColumnTypeName(context,null,null)
            }
            Timber.d("scheme.equals content path:$path")
            return path
        }
        else if(scheme.equals("file",ignoreCase = true)){
            Timber.d("scheme is $scheme")
            path = getPath()
            return path
        }
    }

    else if(scheme.equals("content",ignoreCase = true)){
        Timber.d("is document URI else")
        Timber.d("google photo $scheme")
        path = if(isMedia()){
            Timber.d("isMedia")
            getDataColumn(context,null,null)
        }
        else if(isGooglePhotosUri()){
            Timber.d("isGooglePhotosUri")
            lastPathSegment
        }
        else if(isGooglePhotosProviderUri()){
            getSavedFilePathFromURI(context)
        }
        else if(isGoogleDriveUri()){
            Timber.d("isGoogleDriveUri")
            getSavedFilePathFromURI(context)
        }
        else if(isOneDriveUri()){
            Timber.d("isOneDriveUri")
            getSavedFilePathFromURI(context)
        }
        else{
            getDataColumnTypeName(context,null,null)
        }
        Timber.d("scheme.equals content path:$path")
        return path
    }

    else if(scheme.equals("file",ignoreCase = true)){
        path = getPath()
        return path
    }

    Timber.d("nothing path:${this.path}")
    return path
}


fun getFileName(context: Context,uri:Uri,myAppFileProvider:String):String?{
    Timber.d("getFileName")
    val mimeType = context.contentResolver.getType(uri)
    var fileName :String? = null

    if(mimeType==null){
        Timber.d("mimeType is null")
        val path = getPath(context,uri,myAppFileProvider)
        if(path!=null){
            Timber.d("path is not null")
            fileName = getName(uri.toString())
        }
        else{
            Timber.d("path is null")
            val file = File(context.cacheDir,path)
            fileName = file.name
        }
    }
    else{
        Timber.d("mimeType is not null")
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if(cursor!=null){
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
            cursor.close()
        }
    }
    return fileName
}
fun getPath(context: Context,uri:Uri,myAppFileProvider:String):String{
    Timber.d("getPath")
    val absolPath = uri.getPathFor(context,myAppFileProvider)
    return absolPath ?: uri.toString()
}
fun getName(fileName:String?):String?{
    Timber.d("getName")
    if(fileName==null){
        return null
    }
    val index = fileName.lastIndexOf('/')
    return fileName.substring(index+1)
}
fun getDocumentCacheDir(context: Context):File{
    val dir = File(context.cacheDir, "documents")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    logDir(context.cacheDir)
    logDir(dir)
    return dir
}
fun logDir(dir: File) {
    Timber.d("Dir=$dir")
    val files = dir.listFiles()
    for (file in files) {
        Timber.d("File=" + file.path)
    }
}
fun generateFileName(name:String?,directory:File):File?{
    var newName = name

    if (newName == null) {
        return null
    }

    var file = File(directory, newName)
    if (file.exists()) {
        var fileName = newName
        var extension = ""
        val dotIndex = newName.lastIndexOf('.')
        if (dotIndex > 0) {
            fileName = newName.substring(0, dotIndex)
            extension = newName.substring(dotIndex)
        }
        var index = 0
        while (file.exists()) {
            index++
            newName = "$fileName($index)$extension"
            file = File(directory, newName)
        }
    }

    try {
        if (!file.createNewFile()) {
            return null
        }
    } catch (e: IOException) {
        Timber.d("generateFileName exception :${e.message}")
        return null
    }

    logDir(directory)

    return file
}
fun saveFileFromUri(context: Context,uri: Uri,destinationPath:String){
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        `is` = context.contentResolver.openInputStream(uri)
        bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
        val buf = ByteArray(1024)
        `is`!!.read(buf)
        do {
            bos.write(buf)
        } while (`is`!!.read(buf) != -1)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            `is`?.close()
            bos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}



fun Uri.getDataColumn(context: Context,selection: String?,selectionArgs: Array<String>?):String?{
    Timber.d("getDataColumn")
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    var path :String? = null
    try {
        cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            path  =  cursor.getString(index)
            Timber.d("getDataColumn path: $path")
            return path
        }
    } finally {
        cursor?.close()
    }
    return path
}
fun Uri.getDataColumnTypeName(context: Context,selection: String?,selectionArgs: Array<String>?):String?{
    Timber.d("getDataColumnTypeName")
    var cursor: Cursor? = null
    val column = MediaStore.MediaColumns.DISPLAY_NAME
//    val column = MediaStore.MediaColumns.DATA
    val projection = arrayOf(column)
    var path :String? = null
    try {
        cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
//        cursor = context.contentResolver.query(this, projection, selection, selectionArgs,
//            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            path  =  cursor.getString(index)
            Timber.d("getDataColumnType2 path: $path")
            return path
        }
    } finally {
        cursor?.close()
    }
    return path
}
fun Uri.getDataColumnTypeData(context: Context,selection: String?,selectionArgs: Array<String>?):String?{
    Timber.d("getDataColumnTypeData")
    var cursor: Cursor? = null
    val column = MediaStore.MediaColumns.DATA
    val projection = arrayOf(column)
    var path :String? = null
    try {
        cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
//        cursor = context.contentResolver.query(this, projection, selection, selectionArgs,
//            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            path  =  cursor.getString(index)
            Timber.d("getDataColumnTypeData path: $path")
            return path
        }
    } finally {
        cursor?.close()
    }
    return path
}

fun Uri.getSavedFilePathFromURI(context:Context):String?{
    var path:String?=null
    var cursor: Cursor? = null
    try{
        cursor = context.contentResolver.query(this, null, null, null, null)
        if(cursor!=null){
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            val name = cursor.getString(nameIndex)
            val size = cursor.getLong(sizeIndex).toLong()
            val file = File(context.cacheDir,name)
            try{
                val inputStream = context.contentResolver.openInputStream(this)
                val outputStream = FileOutputStream(file)

                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()
                val bufferSize = min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1)
                {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            }
            catch (e:java.lang.Exception){
                Timber.d("getSavedFilePathFromURI error:${e.message}" )
            }
            path =  file.path
        }

    }
    finally {
        cursor?.close()
    }
    return path
}
fun Uri.getGoogleDriveFilePath(context:Context):String?{
    var path:String?=null
    var cursor: Cursor? = null
    try{
        cursor = context.contentResolver.query(this, null, null, null, null)
        if(cursor!=null){
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            val name = cursor.getString(nameIndex)
            val size = cursor.getLong(sizeIndex).toLong()
            val file = File(context.cacheDir,name)
            try{
                val inputStream = context.contentResolver.openInputStream(this)
                val outputStream = FileOutputStream(file)

                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()
                val bufferSize = min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1)
                {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            }
            catch (e:java.lang.Exception){
                Timber.d("getGoogleDriveFilePath error:${e.message}" )
            }
            path =  file.path
        }

    }
    finally {
        cursor?.close()
    }
    return path
}
fun Uri.getOneDriveFilePath(context:Context):String?{
    var path:String?=null
    var cursor: Cursor? = null
    try{
        cursor = context.contentResolver.query(this, null, null, null, null)
        if(cursor!=null){
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            val name = cursor.getString(nameIndex)
            val size = cursor.getLong(sizeIndex).toLong()
            val file = File(context.cacheDir,name)
            try{
                val inputStream = context.contentResolver.openInputStream(this)
                val outputStream = FileOutputStream(file)

                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()
                val bufferSize = min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1)
                {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            }
            catch (e:java.lang.Exception){
                Timber.d("getOneDriveFilePath error:${e.message}" )
            }
            path =  file.path
        }

    }
    finally {
        cursor?.close()
    }
    return path
}


fun Uri.isDownloadDocument():Boolean{
    return this.authority.equals("com.android.providers.downloads.documents")
}
fun Uri.isMedia():Boolean{
    return this.authority.equals("media")
}
fun Uri.isMediaDocument():Boolean{
    return this.authority.equals("com.android.providers.media.documents")
}
fun Uri.isGooglePhotosUri():Boolean{
    return this.authority.equals("com.google.android.apps.photos.content")
}
fun Uri.isGooglePhotosProviderUri():Boolean{
    return this.authority.equals("com.google.android.apps.photos.contentprovider")
}
fun Uri.isGoogleDriveUri():Boolean{
    return "com.google.android.apps.docs.storage.legacy" == authority || "com.google.android.apps.docs.storage" == authority
}
fun Uri.isExternalStorageDocument():Boolean{
    return this.authority.equals("com.android.externalstorage.documents")
}
fun Uri.isLocalStorageDocument(myAppFileProvider:String):Boolean{
//    return this.authority.equals(MY_APP_FILE_PROVIDER)
    return this.authority.equals(myAppFileProvider)
}
fun Uri.isOneDriveUri():Boolean{
    return this.authority.equals("com.microsoft.skydrive.content.StorageAccessProvider")
}