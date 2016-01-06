package com.millo.abcshare;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.millo.abcshare.MilloHelpers.MyLog;

public class MyWebServer extends NanoHTTPD {
    public static final String TAG = "MyWebServer";

	/**
	 * Common mime type for dynamic content: binary
	 */
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
	/**
	 * Default Index file names.
	 */
	public static final List<String> INDEX_FILE_NAMES = new ArrayList<String>() {{
		add("index.html");
		add("index.htm");
	}};
	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
		put("css", "text/css");
		put("htm", "text/html");
		put("html", "text/html");
		put("xml", "text/xml");
		put("java", "text/x-java-source, text/java");
		put("md", "text/plain");
		put("txt", "text/plain");
		put("asc", "text/plain");
		put("gif", "image/gif");
		put("jpg", "image/jpeg");
		put("jpeg", "image/jpeg");
		put("png", "image/png");
		put("mp3", "audio/mpeg");
		put("m3u", "audio/mpeg-url");
		put("mp4", "video/mp4");
		put("ogv", "video/ogg");
		put("flv", "video/x-flv");
		put("mov", "video/quicktime");
		put("swf", "application/x-shockwave-flash");
		put("js", "application/javascript");
		put("pdf", "application/pdf");
		put("doc", "application/msword");
		put("ogg", "application/x-ogg");
		put("zip", "application/octet-stream");
		put("exe", "application/octet-stream");
		put("class", "application/octet-stream");
	}};
	/**
	 * The distribution licence
	 */
	private static final String LICENCE =
			"Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n"
					+ "\n"
					+ "Redistribution and use in source and binary forms, with or without\n"
					+ "modification, are permitted provided that the following conditions\n"
					+ "are met:\n"
					+ "\n"
					+ "Redistributions of source code must retain the above copyright notice,\n"
					+ "this list of conditions and the following disclaimer. Redistributions in\n"
					+ "binary form must reproduce the above copyright notice, this list of\n"
					+ "conditions and the following disclaimer in the documentation and/or other\n"
					+ "materials provided with the distribution. The name of the author may not\n"
					+ "be used to endorse or promote products derived from this software without\n"
					+ "specific prior written permission. \n"
					+ " \n"
					+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
					+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
					+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
					+ "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
					+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
					+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
					+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
					+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
					+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
					+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

	private static Map<String, WebServerPlugin> mimeTypeHandlers = new HashMap<String, WebServerPlugin>();
	private final List<File> rootDirs;
    private static HashMap<String,File> _files = new HashMap<String, File>();
	private static boolean _DEBUG = false;
	//    WebServerTask _task = null;
	private Context _context;

	private Messenger _messenger;

	private int _connections = 0;

	private HashMap<String, ArrayList<String>> _data;

    private String _host = "";
    private int _port = 0;

    private Intent broadcastIntent;

    protected String getPageTemplate(){
        StringBuilder msg = new StringBuilder();
        msg.append(HtmlFormatter.html_start());
        msg.append(HtmlFormatter.head("ABC Share"));
        msg.append(HtmlFormatter.body_start());
        msg.append(HtmlFormatter.nav_start());
        msg.append(HtmlFormatter.a(makeUrl("home", ""), "HOME"));
        msg.append(HtmlFormatter.a(makeUrl("download", ""), "DOWNLOAD"));
        msg.append(HtmlFormatter.a(makeUrl("browse", ""), "BROWSE"));
        msg.append(HtmlFormatter.nav_end());
        msg.append(HtmlFormatter.header("%%heading%%"));

        msg.append("%%body%%");

        msg.append(HtmlFormatter.footer("ABC Share - Copyright MilloSoft"));
        msg.append(HtmlFormatter.body_end());
        msg.append(HtmlFormatter.html_end());

        return msg.toString();
    }

	//   private Context ctx = null;

	//    public MyWebServer(String host, int port, File wwwroot, boolean quiet) {
	//        super(host, port);
	//        this.quiet = quiet;
	//        this.rootDirs = new ArrayList<File>();
	//        this.rootDirs.add(wwwroot);
	//
	//        this.init();
	//    }

	public MyWebServer(Context context,
                       Messenger messenger,
                       Intent intent,
                       String host,
                       int port,
                       HashMap<String, ArrayList<String>> _data2,
                       File wwwroot,
                       boolean debug) {
		super(host, port);

        this._host = host;

		_DEBUG = debug;

                this.rootDirs = new ArrayList<File>();
                this.rootDirs.add(wwwroot);

        _data = _data2;
		//this.rootDirs = new ArrayList<File>(wwwroots);
		//        _task = task;
		_context = context;
		_messenger = messenger;

        broadcastIntent = intent;

		this.init();
	}

	/**
	 * Used to initialize and customize the server.
	 */
	public void init() {
	}

	//    /**
	//     * Starts as a standalone file server and waits for Enter.
	//     */
	//    public static void main(String[] args) {
	//        // Defaults
	//        int port = 8080;
	//
	//        String host = "127.0.0.1";
	//        List<File> rootDirs = new ArrayList<File>();
	//        boolean quiet = false;
	//        Map<String, String> options = new HashMap<String, String>();
	//
	//        // Parse command-line, with short and long versions of the options.
	//        for (int i = 0; i < args.length; ++i) {
	//            if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--host")) {
	//                host = args[i + 1];
	//            } else if (args[i].equalsIgnoreCase("-p") || args[i].equalsIgnoreCase("--port")) {
	//                port = Integer.parseInt(args[i + 1]);
	//            } else if (args[i].equalsIgnoreCase("-q") || args[i].equalsIgnoreCase("--quiet")) {
	//                quiet = true;
	//            } else if (args[i].equalsIgnoreCase("-d") || args[i].equalsIgnoreCase("--dir")) {
	//                rootDirs.add(new File(args[i + 1]).getAbsoluteFile());
	//            } else if (args[i].equalsIgnoreCase("--licence")) {
	//                System.out.println(LICENCE + "\n");
	//            } else if (args[i].startsWith("-X:")) {
	//                int dot = args[i].indexOf('=');
	//                if (dot > 0) {
	//                    String name = args[i].substring(0, dot);
	//                    String value = args[i].substring(dot + 1, args[i].length());
	//                    options.put(name, value);
	//                }
	//            }
	//        }
	//
	//        if (rootDirs.isEmpty()) {
	//            rootDirs.add(new File(".").getAbsoluteFile());
	//        }
	//
	//        options.put("host", host);
	//        options.put("port", ""+port);
	//        options.put("quiet", String.valueOf(quiet));
	//        StringBuilder sb = new StringBuilder();
	//        for (File dir : rootDirs) {
	//            if (sb.length() > 0) {
	//                sb.append(":");
	//            }
	//            try {
	//                sb.append(dir.getCanonicalPath());
	//            } catch (IOException ignored) {}
	//        }
	//        options.put("home", sb.toString());
	//
	//        ServiceLoader<WebServerPluginInfo> serviceLoader = ServiceLoader.load(WebServerPluginInfo.class);
	//        for (WebServerPluginInfo info : serviceLoader) {
	//            String[] mimeTypes = info.getMimeTypes();
	//            for (String mime : mimeTypes) {
	//                String[] indexFiles = info.getIndexFilesForMimeType(mime);
	//                if (!quiet) {
	//                	Log.i(TAG,"# Found plugin for Mime type: \"" + mime + "\"");
	//                    System.out.print("# Found plugin for Mime type: \"" + mime + "\"");
	//                    if (indexFiles != null) {
	//                        Log.i(TAG," (serving index files: ");
	//                        System.out.print(" (serving index files: ");
	//                        for (String indexFile : indexFiles) {
	//                        	Log.i(TAG,indexFile + " ");
	//                            System.out.print(indexFile + " ");
	//                        }
	//                    }
	//                    Log.i(TAG,").");
	//                    System.out.println(").");
	//                }
	//                registerPluginForMimeType(indexFiles, mime, info.getWebServerPlugin(mime), options);
	//            }
	//        }
	//
	//        ServerRunner.executeInstance(new MyWebServer(host, port, rootDirs, quiet));
	//    }

	//    protected static void registerPluginForMimeType(String[] indexFiles, String mimeType, WebServerPlugin plugin, Map<String, String> commandLineOptions) {
	//        if (mimeType == null || plugin == null) {
	//            return;
	//        }
	//
	//        if (indexFiles != null) {
	//            for (String filename : indexFiles) {
	//                int dot = filename.lastIndexOf('.');
	//                if (dot >= 0) {
	//                    String extension = filename.substring(dot + 1).toLowerCase();
	//                    MIME_TYPES.put(extension, mimeType);
	//                }
	//            }
	//            INDEX_FILE_NAMES.addAll(Arrays.asList(indexFiles));
	//        }
	//        mimeTypeHandlers.put(mimeType, plugin);
	//        plugin.initialize(commandLineOptions);
	//    }

	//    private File getRootDir() {
	//        return rootDirs.get(0);
	//    }

//	    private Map<String,File> getRootDirs() {
//	        return _files;
//	    }

	//    private void addWwwRootDir(File wwwroot) {
	//        rootDirs.add(wwwroot);
	//    }

	/**
	 * URL-encodes everything between "/"-characters. Encodes spaces as '%20' instead of '+'.
	 */
	private String encodeUri(String uri) {
		String newUri = "";
		StringTokenizer st = new StringTokenizer(uri, "/ ", true);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("/"))
				newUri += "/";
			else if (tok.equals(" "))
				newUri += "%20";
			else {
				try {
					newUri += URLEncoder.encode(tok, "UTF-8");
				} catch (UnsupportedEncodingException ignored) {
				}
			}
		}
		return newUri;
	}

    private String makeUrl(String action, String uri){
        String URL = "";
        if (!uri.startsWith("/")) uri = "/"+uri;
        URL = "/"+action+uri;
        //return "http://"+_host+":"+this.getListeningPort()+URL;
        return URL;
    }

	public Response serve(IHTTPSession session) {
		Log.i(TAG, "MyWebServer::serve START");

		//    	_task.handleDecodeState(DECODE_STATE_COMPLETED);

		Map<String, String> header = session.getHeaders();
		Map<String, String> parms = session.getParms();

        Log.i(TAG, "getQueryParameterString: "+session.getQueryParameterString());
        Log.i(TAG, "getMethod: "+session.getMethod());

        String uri = session.getUri();
        Log.i(TAG, "getUri: "+uri);

        Iterator<String> e = header.keySet().iterator();
        while (e.hasNext()) {
            String value = e.next();
            Log.i(TAG,"  HDR: '" + value + "' = '" + header.get(value) + "'");
        }
        e = parms.keySet().iterator();
        while (e.hasNext()) {
            String value = e.next();
            Log.i(TAG,"  PRM: '" + value + "' = '" + parms.get(value) + "'");
        }

		//        //for (<String,File> homeDir : getRootDirs()) {
		//        for (Entry<String, File> entry : _files.entrySet()) {
		//            // Make sure we won't die of an exception later
		//            if (!homeDir.isDirectory()) {
		//                return getInternalErrorResponse("given path is not a directory (" + homeDir + ").");
		//            }
		//        }
		Log.i(TAG,"MyWebServer::serve END");
		return respond(Collections.unmodifiableMap(header), session, uri);
	}

//	boolean CreateZip(String[] srcFiles, String zipFile){
//		try {
//
//			// create byte buffer
//			byte[] buffer = new byte[1024];
//
//			FileOutputStream fos = new FileOutputStream(zipFile);
//
//			ZipOutputStream zos = new ZipOutputStream(fos);
//			zos.setLevel(Deflater.NO_COMPRESSION);
//
//			for (int i=0; i < srcFiles.length; i++) {
//
//				String uripath = srcFiles[i];
//				Uri uri = Uri.parse(uripath);
//				//String filePath = MilloHelpers.getFilePathByUri(_context,uri);
//				String filePath = MilloHelpers.UriResolver.getPath(_context,uri);
//				Log.i(TAG,"File:"+filePath);
//				File srcFile = new File(filePath);
//
//				FileInputStream fis = new FileInputStream(srcFile);
//
//				// begin writing a new ZIP entry, positions the stream to the start of the entry data
//				zos.putNextEntry(new ZipEntry(srcFile.getName()));
//
//				int length;
//
//				while ((length = fis.read(buffer)) > 0) {
//					zos.write(buffer, 0, length);
//				}
//
//				zos.closeEntry();
//
//				// close the InputStream
//				fis.close();
//			}
//
//			// close the ZipOutputStream
//			zos.close();
//		}
//		catch (IOException ioe) {
//			System.out.println("Error creating zip file: " + ioe);
//			return false;
//		}
//		return true;
//	}

	private Response respond(Map<String, String> headers, IHTTPSession session, String uri) {
		Log.i(TAG,"MyWebServer::respond START");

		Response response = null;

		//    	_files = null;
		//    	_files = Helpers.loadMap(_context);

		//    	Uri parsedUri = Uri.parse(uri);
		//    	if (parsedUri.getPath()==""){

		// Remove URL arguments
        Log.i(TAG,"Received uri: "+uri);
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}

        if (uri.contentEquals("/")){
            Log.i(TAG,"uri is root");
            return getHomeResponse(uri);
        }

        String api = "";
        Log.i(TAG,"uri: "+uri);
        if (uri.startsWith("/")){
            String[] apis = uri.toString().split("/");
            if (apis.length>1){
                api = apis[1].toLowerCase();
                Log.i(TAG,"api: ["+api+"]");
            }
            else{
                return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, "incorrect url");
            }
        }

        Log.i(TAG,"uri: "+uri);

        int iii = uri.indexOf('/', 1);
        if (iii!=-1)
            uri = uri.substring(iii);
        else
            uri = "/";

        Log.i(TAG,"uri: "+uri);
        Log.i(TAG,"api: "+api);
        if (api.contentEquals("download")){
            Log.i(TAG,"api: DOWNLOAD");

            if (_data.size()<=0){
                String template = getPageTemplate();
                template = template.replace("%%heading%%", "Shared files");
                template = template.replace("%%body%%", HtmlFormatter.title("No file available to share"));
                return createResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, template);
            }

            if (_data.size()>0) {
                String key = (String) _data.keySet().toArray()[0];
                ArrayList<String> val = _data.get(key);
                Log.i(TAG, "key: " + key);
                Log.i(TAG, "val: " + val.toString());

                if (key.equals(Settings.INTENTEXTRA_TEXT)) {
                    String text = "";
                    for (String s : val) {
                        text += "\r\n" + s;
                    }
                    response = createResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, text);
                } else if (key.equals(Settings.INTENTEXTRA_STREAM)) {

                    //        File f = (File) _files.values().toArray()[0];

                    Uri data = null;
                    if (val.size() > 1) {

                        //**** MULTIPLE FILES ****//
                        ArrayList<String> files = new ArrayList<String>();
                        for (String u : val) {
                            Uri uri1 = Uri.parse(u);
                            String file1 = MilloHelpers.UriResolver.getPath(_context, uri1);
                            if (_DEBUG) Log.i(TAG, "MyWebServer::respond filePath: " + file1);
                            files.add(file1);
                        }
                        String[] newfiles = files.toArray(new String[files.size()]);
                        if (_DEBUG) for (String ss : newfiles)
                            Log.i(TAG, "MyWebServer::respond new filePath: " + ss);
                        return new ResponseMulti("text", newfiles);

                        //				File outputDir = _context.getCacheDir(); // context being the Activity pointer
                        //				Log.i(TAG,"MyWebServer::respond outputDir: "+outputDir);
                        //
                        //				File outputFile;
                        //				try {
                        //					outputFile = File.createTempFile("simpleshare", ".zip", outputDir);
                        //					Log.i(TAG,"MyWebServer::respond outputFile: "+outputFile);
                        //				} catch (IOException e) {
                        //					return getInternalErrorResponse("cannot create temporary file");
                        //				}
                        //				String[] files = val.toArray(new String[val.size()]);
                        //				if (!CreateZip(files, outputFile.getAbsolutePath().toString()))
                        //					return getInternalErrorResponse("cannot create zip file");
                        //				data = Uri.fromFile(outputFile);
                        //				Log.i(TAG,"MyWebServer::respond data: "+data);

                    } else {
                        data = Uri.parse(val.get(0));
                    }

                    //String filePath = MilloHelpers.getFilePathByUri(_context,data);

                    String filePath = MilloHelpers.UriResolver.getPath(_context, data);
                    Log.i(TAG, "MyWebServer::respond filePath: " + filePath);

                    File f = new File(filePath);
                    Log.i(TAG, "MyWebServer::respond file: " + f.getName());

                    String mimeTypeForFile = getMimeTypeForFile(uri);
                    Log.i(TAG, "MyWebServer::respond mime: " + mimeTypeForFile);
                    response = serveFile(uri, headers, f, mimeTypeForFile);

                    if (_DEBUG) {
                        Iterator<String> e = response.header.keySet().iterator();
                        while (e.hasNext()) {
                            String value = e.next();
                            Log.i(TAG, "  RES-HDR: '" + value + "' = '" + response.header.get(value) + "'");
                        }
                    }
                }
            }
        }
        else if (api.contentEquals("home")) {
            Log.i(TAG, "api: HOME");
            return getHomeResponse(uri);
        }
        else if (api.contentEquals("browse")){
            Log.i(TAG,"api: BROWSE");

            // Prohibit getting out of current directory
            if (uri.startsWith("src/main") || uri.endsWith("src/main") || uri.contains("../")) {
                return getForbiddenResponse("Won't serve ../ for security reasons.");
            }

            boolean canServeUri = false;
            File homeDir = null;
            //List<File> roots = getRootDirs();
            for (int i = 0; !canServeUri && i < rootDirs.size(); i++) {
                homeDir = rootDirs.get(i);
                canServeUri = canServeUri(uri, homeDir);
            }
            if (!canServeUri) {
                return getNotFoundResponse();
            }

            Log.i(TAG, "Needs redirect?");
            // Browsers get confused without '/' after the directory, send a redirect.
            File f = new File(homeDir, uri);
            if (f.isDirectory() && !uri.endsWith("/")) {
                uri += "/";
                Response res = createResponse(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, "<html><body>Redirected: <a href=\"" +
                        uri + "\">" + uri + "</a></body></html>");
                res.addHeader("Location", uri);
                return res;
            }

            Log.i(TAG, "Is directory?");
            if (f.isDirectory()) {
                // token table
//			        	return createResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, listTokens(uri));

                // First look for index files (index.html, index.htm, etc) and if none found, list the directory if readable.
                String indexFile = findIndexFileInDirectory(f);
                if (indexFile == null) {
                    if (f.canRead()) {
                        // No index file, list the directory if it is readable
                        return createResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, listDirectory(uri, f));
                    } else {
                        return getForbiddenResponse("No directory listing.");
                    }
                } else {
                    return respond(headers, session, uri + indexFile);
                }
            }

            Log.i(TAG, "Is mime type?");
            String mimeTypeForFile = getMimeTypeForFile(uri);
            WebServerPlugin plugin = mimeTypeHandlers.get(mimeTypeForFile);
            //Response response = null;

            Log.i(TAG, "Is plugin?");
            if (plugin != null) {
                response = plugin.serveFile(uri, headers, session, f, mimeTypeForFile);
                if (response != null && response instanceof InternalRewrite) {
                    InternalRewrite rewrite = (InternalRewrite) response;
                    return respond(rewrite.getHeaders(), session, rewrite.getUri());
                }
            } else {
                response = serveFile(uri, headers, f, mimeTypeForFile);
            }
        }
        else{
            Log.i(TAG,"api: OTHER");
            return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, "incorrect api: "+api);
        }

		//        String token = uri.replace("/", "");
		//        
		//        if (!_files.containsKey(token)){
		//        	return getNotFoundResponse();
		//        }
		//        
		//        File f = (File) _files.get(token);
		//        Log.i(TAG,"MyWebServer::respond file:"+f.getName());

		Log.i(TAG,"MyWebServer::respond END");

		return response != null ? response : getNotFoundResponse();
	}

	String HtmlDoc(String text){
		return "<html><head><title></title></head><body>"+text+"</body></html>";
	}

		String listTokens(String uri){
//			String s = "";
//			s += "<html><head><title>test</title><body>";
//			for (Entry<String, File> entry : _files.entrySet()) {
//				String token = entry.getKey();
//				File f = entry.getValue();
//				s+= "<a href=\""+encodeUri(uri + token)+"\">"+f.getName()+"</a></br>";
//			}
//			s += "</body></html>";
//			return s;


            String s = "";
            s+=HtmlFormatter.html_start();
            s+=HtmlFormatter.head("ABC Share");
            s+=HtmlFormatter.body_start();

            for (Entry<String, File> entry : _files.entrySet()) {
                String token = entry.getKey();
                File f = entry.getValue();
                //s+= "<a href=\""+encodeUri(uri + token)+"\">"+f.getName()+"</a></br>";
                s+= HtmlFormatter.item(HtmlFormatter.a(encodeUri(uri + token), f.getName()));
            }
            s+=HtmlFormatter.body_end();
            s+=HtmlFormatter.html_end();
            return s;
		}

	protected Response getNotFoundResponse() {
		return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, CreateMessage("Error 404, file not found."));
	}

	protected Response getForbiddenResponse(String s) {
		return createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_HTML, CreateMessage("FORBIDDEN: " + s));
	}

	protected Response getInternalErrorResponse(String s) {
		return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, CreateMessage("INTERNAL ERROR: " + s));
	}

    protected Response getHomeResponse(String uri) {

        StringBuilder msg = new StringBuilder();
        msg.append(HtmlFormatter.section_start());
        msg.append(HtmlFormatter.a(makeUrl("download", uri), "Download shared file"));
        msg.append(HtmlFormatter.section_end());
        msg.append(HtmlFormatter.section_start());
        msg.append(HtmlFormatter.a(makeUrl("browse", uri), "Browse device folders"));
        msg.append(HtmlFormatter.section_end());

        String template = getPageTemplate();
        template = template.replace("%%heading%%", "Select an action");
        template = template.replace("%%body%%", msg.toString());
        return createResponse(
                Response.Status.OK,
                NanoHTTPD.MIME_HTML,
                template
                );
    }

    private String CreateMessage(String s){
        StringBuilder msg = new StringBuilder();
        //msg.append(HtmlFormatter.html_start());
        //msg.append(HtmlFormatter.head("ABC Share"));
        //msg.append(HtmlFormatter.body_start());
        msg.append(HtmlFormatter.title(s));
        //msg.append(HtmlFormatter.body_end());
        //msg.append(HtmlFormatter.html_end());

        String template = getPageTemplate();
        template = template.replace("%%heading%%", "Error");
        template = template.replace("%%body%%", msg.toString());
        return template;
    }

	    private boolean canServeUri(String uri, File homeDir) {

	    	Log.i(TAG,"MyWebServer::canServeUri START");
	    	Log.i(TAG,"uri:"+ uri);
	    	Log.i(TAG,"homeDir:"+ homeDir);

	        boolean canServeUri;
	        File f = new File(homeDir, uri);
	        canServeUri = f.exists();
	        if (!canServeUri) {
	            String mimeTypeForFile = getMimeTypeForFile(uri);
	            WebServerPlugin plugin = mimeTypeHandlers.get(mimeTypeForFile);
	            if (plugin != null) {
	                canServeUri = plugin.canServeUri(uri, homeDir);
	            }
	        }
            Log.i(TAG,"canServeUri:"+ canServeUri);
	    	Log.i(TAG,"MyWebServer::canServeUri END");
	        return canServeUri;
	    }

	/**
	 * Serves file from homeDir and its' subdirectories (only). Uses only URI, ignores all headers and HTTP parameters.
	 */
	Response serveFile(String uri, Map<String, String> header, File file, String mime) {
		Log.i(TAG,"MyWebServer::serveFile START");
		Log.i(TAG,"uri: "+ uri);
		Log.i(TAG,"path: "+ file.getAbsolutePath());
		Log.i(TAG,"file: "+ file.getName());
		Log.i(TAG,"mime: "+ mime);

		Response res;
		try {
			// Calculate etag
			String etag = Integer.toHexString(
					(file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());

			// Support (simple) skipping:
			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}

			// Change return code and add Content-Range header when skipping is requested
			long fileLen = file.length();
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
					res.addHeader("ETag", etag);
				} else {
					if (endAt < 0) {
						endAt = fileLen - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					final long dataLen = newLen;
					FileInputStream fis = new FileInputStream(file) {
						@Override
						public int available() throws IOException {
							return (int) dataLen;
						}
					};
					fis.skip(startFrom);

					res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
					res.addHeader("Content-Length", "" + dataLen);

					//Content-Disposition: attachment; filename=FILENAME
					res.addHeader("Content-Disposition", "attachment; filename=\""+encodeUri(file.getName())+"\"");

					res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
				else {
					Log.i(TAG,"MyWebServer::serveFile sending full file...");
					res = createResponse(Response.Status.OK, mime, new FileInputStream(file));
					res.addHeader("Content-Length", "" + fileLen);

					//Content-Disposition: attachment; filename=FILENAME
					res.addHeader("Content-Disposition", "attachment;filename=\""+encodeUri(file.getName())+"\"");

					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			res = getForbiddenResponse("Reading file failed.");
		}
		Log.i(TAG,"MyWebServer::serveFile END");

		return res;
	}

	//Response serveZip(String uri, Map<String, String> header, final String[] srcFiles, String mime) {
	//	Log.i(TAG,"MyWebServer::serveFile START");
	//	Log.i(TAG,"uri: "+ uri);
	//	Log.i(TAG,"path: "+ file.getAbsolutePath());
	//	Log.i(TAG,"file: "+ file.getName());
	//	Log.i(TAG,"mime: "+ mime);
	//
	//	Response res;
	//	try {
	//		// Calculate etag
	////		String etag = Integer.toHexString(
	////				(file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());
	//		
	//		// create byte buffer
	//		
	//	
	//					byte[] buffer = new byte[1024];
	//
	//					//FileOutputStream fos = new FileOutputStream(zipFile);
	//
	//					ZipOutputStream zos = new ZipOutputStream(new OutputStream() {
	//						
	//						@Override
	//						public void write(int oneByte) throws IOException {
	//							// TODO Auto-generated method stub
	//							for (int i=0; i < srcFiles.length; i++) {
	//
	//								String uripath = srcFiles[i];
	//								Uri uri = Uri.parse(uripath);
	//								//String filePath = MilloHelpers.getFilePathByUri(_context,uri);
	//								String filePath = MilloHelpers.UriResolver.getPath(_context,uri);
	//								File srcFile = new File(filePath);
	//
	//								FileInputStream fis = new FileInputStream(srcFile);
	//
	//								// begin writing a new ZIP entry, positions the stream to the start of the entry data
	//								putNextEntry(new ZipEntry(srcFile.getName()));
	//
	//								int length;
	//
	//								while ((length = fis.read(buffer)) > 0) {
	//									zos.write(buffer, 0, length);
	//								}
	//
	//								zos.closeEntry();
	//
	//								// close the InputStream
	//								fis.close();
	//							}
	//							
	//						}
	//					});
	//
	//
	//					// close the ZipOutputStream
	//					zos.close();
	//
	//				Log.i(TAG,"MyWebServer::serveFile sending full file...");
	//				res = createResponse(Response.Status.OK, mime, new FileInputStream(file));
	////				res.addHeader("Content-Length", "" + fileLen);
	//
	//				//Content-Disposition: attachment; filename=FILENAME
	//				res.addHeader("Content-Disposition", "attachment;filename=\""+encodeUri(file.getName())+"\"");
	//
	////				res.addHeader("ETag", etag);
	//	} catch (IOException ioe) {
	//		res = getForbiddenResponse("Reading file failed.");
	//	}
	//	Log.i(TAG,"MyWebServer::serveFile END");
	//
	//	return res;
	//}


	// Get MIME type from file name extension, if possible
	private String getMimeTypeForFile(String uri) {
		int dot = uri.lastIndexOf('.');
		String mime = null;
		if (dot >= 0) {
			mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
		}
		return mime == null ? MIME_DEFAULT_BINARY : mime;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType, InputStream message) {
        Log.i(TAG,"createResponse"); //: "+message);
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType, String message) {
        Log.i(TAG, "createResponse"); //: "+message);
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	/* (non-Javadoc)
	 * @see com.millo.abcshare.NanoHTTPD#start()
	 */
	@Override
	public void start() throws IOException {
		Log.i(TAG,"MyWebServer::start START");
		super.start();
		//		NotifyTaskbar(Settings.NOTIFICATION_TITLE, Settings.NOTIFICATION_TEXT);
		sendMessage(Settings.WEBSERVER_STARTED);
        sendGUI(broadcastIntent, Settings.TYPE_STARTED, "1");
		Log.i(TAG, "MyWebServer::start STOP");
	}

	/* (non-Javadoc)
	 * @see com.millo.abcshare.NanoHTTPD#stop()
	 */
	@Override
	public void stop() {
		Log.i(TAG, "MyWebServer::stop START");
		sendMessage(Settings.WEBSERVER_STOPPED);
        sendGUI(broadcastIntent, Settings.TYPE_STARTED, "0");
		//		cancelNotification();
		super.stop();
		Log.i(TAG,"MyWebServer::stop STOP");
	}

	/* (non-Javadoc)
	 * @see com.millo.abcshare.NanoHTTPD#registerConnection(java.net.Socket)
	 */
	@Override
	public synchronized void registerConnection(Socket socket) {
		Log.i(TAG,"MyWebServer::registerConnection START");
		super.registerConnection(socket);
		_connections++;
		//sendMessage(Settings.WEBSERVER_CONNECTED);
		sendMessage(Settings.WEBSERVER_CONNECTED);

        sendGUI(broadcastIntent, Settings.TYPE_CONNECTED, "1");
		Log.i(TAG,"MyWebServer::registerConnection STOP");
	}

	/* (non-Javadoc)
	 * @see com.millo.abcshare.NanoHTTPD#unRegisterConnection(java.net.Socket)
	 */
	@Override
	public synchronized void unRegisterConnection(Socket socket) {
		Log.i(TAG,"MyWebServer::unRegisterConnection START");
		super.unRegisterConnection(socket);
		_connections--;
		//sendMessage(Settings.WEBSERVER_DISCONNECTED);
		sendMessage(Settings.WEBSERVER_DISCONNECTED);
        sendGUI(broadcastIntent, Settings.TYPE_CONNECTED, "0");
		Log.i(TAG,"MyWebServer::unRegisterConnection STOP");
	}

	    private String findIndexFileInDirectory(File directory) {
	        for (String fileName : INDEX_FILE_NAMES) {
	            File indexFile = new File(directory, fileName);
	            if (indexFile.exists()) {
	                return fileName;
	            }
	        }
	        return null;
	    }

	    protected String listDirectory(String uri, File f) {
	    	Log.i(TAG,"MyWebServer::listDirectory START");

	        String heading = "Directory " + uri;

	        StringBuilder msg = new StringBuilder();
//            msg.append(HtmlFormatter.html_start());
//            msg.append(HtmlFormatter.head("ABC Share"));
//            msg.append(HtmlFormatter.body_start());
//            msg.append(HtmlFormatter.nav_start());
//            msg.append(HtmlFormatter.a(makeUrl("", ""), "HOME"));
//            msg.append(HtmlFormatter.a(makeUrl("download", ""), "DOWNLOAD"));
//            msg.append(HtmlFormatter.a(makeUrl("browse", ""), "BROWSE"));
//            msg.append(HtmlFormatter.nav_end());
//            msg.append(HtmlFormatter.header(heading));

//            "<html><head><title>" + heading + "</title><style><!--\n" +
//	            "span.dirname { font-weight: bold; }\n" +
//	            "span.filesize { font-size: 75%; }\n" +
//	            "// -->\n" +
//	            "</style>" +
//	            "</head><body><h1>" + heading + "</h1>");

//            Uri u = Uri.parse(uri);
//            String up = u.getHost()+":"+u.getPort()+"/browse/"+u.getPath();

	        String up = null;
	        if (uri.length() > 1) {
	            String u = uri.substring(0, uri.length() - 1);
	            int slash = u.lastIndexOf('/');
	            if (slash >= 0 && slash < u.length()) {
	                up = uri.substring(0, slash + 1);
                    //up = "/browse"+up;
	            }
	        }
            Log.i(TAG, "uri: " + uri + " up: " + up);

	        List<String> files = Arrays.asList(f.list(new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return new File(dir, name).isFile();
	            }
	        }));
	        Collections.sort(files);
            Log.i(TAG, "files: " + files.size());

	        List<String> directories = Arrays.asList(f.list(new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return new File(dir, name).isDirectory();
	            }
	        }));
	        Collections.sort(directories);
            Log.i(TAG, "directories: " + directories.size());

	        if (up != null || directories.size() + files.size() > 0) {
	            //msg.append("<ul>");
	            if (up != null || directories.size() > 0) {
	                //msg.append("<section class=\"directories\">");

                    msg.append(HtmlFormatter.section_start());
                    msg.append(HtmlFormatter.title("Directories"));

	                if (up != null) {
	                    //msg.append("<li><a rel=\"directory\" href=\"").append(up).append("\"><span class=\"dirname\">..</span></a></b></li>");

                        msg.append(HtmlFormatter.item(HtmlFormatter.a(encodeUri(makeUrl("browse", up)), "..")));
	                }
	                for (String directory : directories) {
	                    String dir = directory + "/";
	                    //msg.append("<li><a rel=\"directory\" href=\"").append(encodeUri(uri + dir)).append("\"><span class=\"dirname\">").append(dir).append("</span></a></b></li>");

                        msg.append(HtmlFormatter.item(HtmlFormatter.a(encodeUri(makeUrl("browse",uri + dir)), dir)));
	                }
	                //msg.append("</section>");

                    msg.append(HtmlFormatter.section_end());
	            }
	            if (files.size() > 0) {
	                //msg.append("<section class=\"files\">");

                    msg.append(HtmlFormatter.section_start());
                    msg.append(HtmlFormatter.title("Files"));

	                for (String file : files) {
	                    //msg.append("<li><a href=\"").append(encodeUri(uri + file)).append("\"><span class=\"filename\">").append(file).append("</span></a>");

                        File curFile = new File(f, file);
	                    long len = curFile.length();
                        String size = "";
	                    //msg.append("&nbsp;<span class=\"filesize\">(");
	                    if (len < 1024) {
	                        size = len + " bytes";
	                    } else if (len < 1024 * 1024) {
                            size = (len / 1024) + "." + + (len % 1024 / 10 % 100) + " KB";
	                    } else {
	                        size = (len / (1024 * 1024)) + "." + (len % (1024 * 1024) / 10 % 100) + " MB";
	                    }
                        //msg.append(")</span></li>");

                        msg.append(HtmlFormatter.item(HtmlFormatter.a(encodeUri(makeUrl("browse", uri + file)), file + " ("+size+")")));

                    }
	                //msg.append("</section>");

                    msg.append(HtmlFormatter.section_end());
	            }
	            //msg.append("</ul>");
	        }

//	        msg.append(HtmlFormatter.body_end());
//            msg.append(HtmlFormatter.html_end());

	    	Log.i(TAG, "MyWebServer::listDirectory END");

            String template = getPageTemplate();
            template = template.replace("%%heading%%", "Browse folders");
	        template = template.replace("%%body%%", msg.toString());
            return template;
	    }

	public void sendMessage(int state) {
		Message message = Message.obtain();
		Bundle b = new Bundle();
		b.putInt("state", state);
		message.setData(b);
		try {
            if (_messenger!=null)
			    _messenger.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	//	//	@SuppressLint("NewApi")
	//	void NotifyTaskbar(String title, String text){
	//		NotificationManager mNotificationManager =
	//				(NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
	//
	//		NotificationCompat.Builder mBuilder =
	//				new NotificationCompat.Builder(_context)
	//		.setSmallIcon(R.drawable.ic_launcher)
	//		.setContentTitle(title)
	//		.setContentText(text)
	//		.setOngoing(true);
	//
	//		Intent resultIntent = new Intent(_context, SimpleShareActivity.class);
	////		if(Build.VERSION.SDK_INT >= 16 ){
	////			// Creates an explicit intent for an Activity in your app
	////
	////			// The stack builder object will contain an artificial back stack for the
	////			// started Activity.
	////			// This ensures that navigating backward from the Activity leads out of
	////			// your application to the Home screen.
	////			TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
	////			// Adds the back stack for the Intent (but not the Intent itself)
	////			stackBuilder.addParentStack(MainActivity.class);
	////			// Adds the Intent that starts the Activity to the top of the stack
	////			stackBuilder.addNextIntent(resultIntent);
	////			PendingIntent resultPendingIntent =
	////					stackBuilder.getPendingIntent(
	////							0,
	////							PendingIntent.FLAG_UPDATE_CURRENT
	////							);
	////			mBuilder.setContentIntent(resultPendingIntent);
	////		}
	////		else{
	//			PendingIntent contentIntent = PendingIntent.getActivity(_context, 0, resultIntent, 0);
	////					Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	//			mBuilder.setContentIntent(contentIntent);
	////		}
	//		mNotificationManager.notify(Settings.NOTIFICATION_ID, mBuilder.build());		
	//	}
	//	void cancelNotification(){
	//		NotificationManager mNotificationManager =
	//				(NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
	//		mNotificationManager.cancel(Settings.NOTIFICATION_ID);
	//	}

	/* (non-Javadoc)
	 * @see com.millo.abcshare.NanoHTTPD#closeAllConnections()
	 */
	@Override
	public synchronized void closeAllConnections() {
		Log.i(TAG,"MyWebServer::closeAllConnections START");
		super.closeAllConnections();
		Log.i(TAG,"MyWebServer::closeAllConnections STOP");
	}

	public static class ResponseMulti extends Response {

		String[] _files;
		public ResponseMulti(String msg, String[] files) {
			super(msg);
			// TODO Auto-generated constructor stub
			_files = files.clone();
		}

		@Override
		protected void send(OutputStream outputStream) {
			Log.i(TAG,"MyWebServer::ResponseMulti::send START");
			//      ///*********************************************
			ZipOutputStream zipOpStream = new ZipOutputStream(outputStream);
			//                 MilloHelpers.UriResolver.getPath(_context, uri);
			try {
				for(String s:_files){
					if(_DEBUG)Log.i(TAG,"MyWebServer::ResponseMulti::send filepath: "+s);
					//  			sendFile(zipOpStream, new File("/storage/emulated/0/DCIM/Camera/20150303_152747_001.jpg"));
					//            sendFile(zipOpStream, new File("/storage/emulated/0/DCIM/Camera/20150221_154227_001.jpg"));
					//            sendFile(zipOpStream, new File("/storage/emulated/0/DCIM/Camera/20150203_121656.jpg"));    		  
					sendFile(zipOpStream, new File(s));    		  
				}

				zipOpStream.flush();
				zipOpStream.close();
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i(TAG,"MyWebServer::ResponseMulti::send END");

		}
		
        public void sendFile(ZipOutputStream zipOpStream, File file) throws Exception {
        	Log.i(TAG,"MyWebServer::ResponseMulti::sendFile START");
        	
  		  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
  		  byte[] fileByte = new byte[4096];
  		  int readBytes = 0;
  		  CRC32 crc = new CRC32();
  		  while (0 != (readBytes = bis.read(fileByte))) {
  		   if(-1 == readBytes){
  		    break;
  		   }
  		   //System.out.println("length::"+readBytes);
  		   crc.update(fileByte, 0, readBytes);
  		  }
  		  bis.close();
  		  
  		  ZipEntry zipEntry = new ZipEntry(file.getName());
  		  zipEntry.setMethod(ZipEntry.STORED);
  		  zipEntry.setCompressedSize(file.length());
  		  zipEntry.setSize(file.length());
  		  zipEntry.setCrc(crc.getValue());
  		  zipOpStream.putNextEntry(zipEntry);
  		  bis = new BufferedInputStream(new FileInputStream(file));
  		  //System.out.println("zipEntryFileName::"+zipEntryFileName);
  		  while (0 != (readBytes = bis.read(fileByte))) {
  		   if(-1 == readBytes){
  		    break;
  		   }
  		   
  		   zipOpStream.write(fileByte, 0, readBytes);
  		  }
  		  bis.close();
      	Log.i(TAG,"MyWebServer::ResponseMulti::sendFile STOP");

  		 }

	}

    public void sendGUI(Intent i, int command, String value) {
        i.putExtra("type", command);
        i.putExtra("value", value);
        _context.sendBroadcast(i);
    }
}
