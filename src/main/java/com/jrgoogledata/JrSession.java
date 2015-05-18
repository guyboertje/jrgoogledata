/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jrgoogledata;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.PemReader.Section;
import com.google.api.client.util.SecurityUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.List;

import org.jruby.RubyHash;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.Visibility;
import org.jruby.runtime.builtin.IRubyObject;
/**
 *
 * @author guy
 */
@JRubyClass(name = "JrGoogleData::Session", parent = "Object")
public class JrSession extends RubyObject {

    private GoogleCredential _credential;
    private String _appName;
    private Drive _drive;
    private SpreadsheetService _spreadsheetService;

    private static final String SPREADSHEET_FEED_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full/";



    public static final ObjectAllocator GDSESSION_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
            return new JrSession(runtime, klass);
        }
    };

    public JrSession(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    @JRubyMethod(name = "new", required = 1, meta = true)
    public static JrSession newInstance(IRubyObject self, IRubyObject arg) {
        JrSession session = (JrSession) ((RubyClass) self).allocate();
        session.callInit(arg, Block.NULL_BLOCK);
        return session;
    }

    @JRubyMethod(required = 1, visibility = Visibility.PRIVATE)
    public void initialize(ThreadContext context, IRubyObject arg) {
        using_options(context, arg);
    }

    @JRubyMethod(required = 1)
    public JrSession using_options(ThreadContext context, IRubyObject options) {
        RubyHash _opts;
        Ruby _ruby = context.runtime;
        if (options instanceof RubyHash) {
            _opts = (RubyHash) options;
            _appName = _opts.op_aref(context, toSym(context.runtime, "application_name")).toString();
            if (_appName == null){
                _appName = "Unknown: JrGoogleData::Session";
            }
            RubySymbol email_sym = toSym(_ruby, "email");
            RubySymbol private_key_sym = toSym(_ruby, "private_key");
            RubySymbol private_key_id_sym = toSym(_ruby, "private_key_id");
            RubySymbol scopes_sym = toSym(_ruby, "scopes");

            if (_opts.has_key_p(email_sym).isFalse()){
                throw _ruby.newArgumentError("Options hash key value :email is missing");
            }

            if (_opts.has_key_p(private_key_sym).isFalse()){
                throw _ruby.newArgumentError("Options hash key value :private_key is missing");
            }

            if (_opts.has_key_p(scopes_sym).isFalse()){
                throw _ruby.newArgumentError("Options hash key value :scopes is missing");
            }

            if (_opts.has_key_p(private_key_id_sym).isFalse()){
                throw _ruby.newArgumentError("Options hash key value :private_key_id is missing");
            }
            try {
            _credential = build_credential(
                    context.runtime,
                    _opts.op_aref(context, email_sym).toString(),
                    _opts.op_aref(context, private_key_sym).toString(),
                    _opts.op_aref(context, scopes_sym).toString(),
                    _opts.op_aref(context, private_key_id_sym).toString()
            );
            }
            catch (GeneralSecurityException e) {
                throw JrCredentialError.newError(context.runtime, e.getLocalizedMessage());
            }
            catch (IOException e) {
                throw context.runtime.newIOErrorFromException(e);
            }
        } else {
            throw context.runtime.newArgumentError("Required argument options hash is missing");
        }

        return this;
    }

    @JRubyMethod(name = "workbook_by_id", required = 1)
    public IRubyObject workbookById(ThreadContext context, IRubyObject id) {
        return workbookById(context, id.toString());
    }

    @JRubyMethod(name = "workbook_by_title", required = 1)
    public IRubyObject workbookByTitle(ThreadContext context, IRubyObject title) {
        String errorMessage = "No error was thrown but no workbook was returned either, hmmmm";
        try {
            String id = idFromTitle(title.toString());
            if ("no-file-found".equals(id)) {
                errorMessage = "File not found with title: " + title;
            } else {
                return workbookById(context, id);
            }
        }
        catch (MalformedURLException e) {
            errorMessage = "MalformedURLException: " + e.getLocalizedMessage();
        }
        catch (GeneralSecurityException e) {
            errorMessage = "GeneralSecurityException: " + e.getLocalizedMessage();
        }
        catch (IOException e) {
            errorMessage = "IOException: " + e.getLocalizedMessage();
        }

        throw JrReadError.newError(context.runtime, errorMessage);
    }

    private IRubyObject workbookById(ThreadContext context, String id) {
        String spreadsheetURL = SPREADSHEET_FEED_URL + id;
        String errorMessage = "No error was thrown but no workbook was returned either, hmmmm";
        try {
            SpreadsheetEntry entry = spreadsheetService().getEntry(new URL(spreadsheetURL), SpreadsheetEntry.class);
            if (entry == null) {
                errorMessage = "Unable to find a workbook with id: " + id;
            } else {
                return new JrWorkbook(
                        context.runtime,
                        entry
                );
            }
        }
        catch (MalformedURLException e) {
            errorMessage = "MalformedURLException: " + e.getLocalizedMessage();
        }
        catch (ServiceException e) {
            errorMessage = "ServiceException: " + e.getLocalizedMessage();
        }
        catch (IOException e) {
            errorMessage = "IOException: " + e.getLocalizedMessage();
        }

        throw JrReadError.newError(context.runtime, errorMessage);
    }

    private String idFromTitle(String title) throws GeneralSecurityException, IOException {
        StringBuilder sb = new StringBuilder("trashed = false and mimeType = 'application/vnd.google-apps.spreadsheet' and ");
        sb.append("title = '").append(title).append("'");

        FileList files = driveService()
                .files().list()
                .setCorpus("DOMAIN")
                .setMaxResults(1)
                .setQ(sb.toString())
                .execute();
        if (files.isEmpty()) {
            return "no-file-found";
        }
        List<File> list = files.getItems();
        if (list.isEmpty()) {
            return "no-file-found";
        }
        File file = list.get(0);
        return file.getId();
    }

    private GoogleCredential build_credential(Ruby runtime, String clientEmail, String privateKey, String scopes, String privateKeyId)
            throws GeneralSecurityException, IOException, RaiseException {

        return new GoogleCredential.Builder()
                .setTransport(getTransport())
                .setJsonFactory(getJsonFactory())
                .setServiceAccountId(clientEmail)
                .setServiceAccountPrivateKey(
                  privateKeyFromPkcs8(runtime, privateKey)
                )
                .setServiceAccountPrivateKeyId(privateKeyId)
                .setServiceAccountScopes(Collections.singleton(scopes))
                .build();

    }

    private SpreadsheetService spreadsheetService() {
        if (_spreadsheetService == null) {
          _spreadsheetService = new SpreadsheetService(_appName);
          _spreadsheetService.setOAuth2Credentials(_credential);
        }
        return _spreadsheetService;
    }

    private Drive driveService() throws GeneralSecurityException, IOException {
        if(_drive == null) {
            _drive = new Drive.Builder(getTransport(), getJsonFactory(), _credential)
                    .setApplicationName(_appName).build();
        }
        return _drive;
    }

    private HttpTransport getTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    private JsonFactory getJsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    private RubySymbol toSym(Ruby runtime, String string) {
        return runtime.newSymbol(string);
    }

    private PrivateKey privateKeyFromPkcs8(Ruby runtime, String privateKeyPem) throws RaiseException {
        Reader reader = new StringReader(privateKeyPem);
        Exception unexpectedException = null;
        try {
            Section section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY");
            if (section == null) {
                unexpectedException = new IOException("Invalid PKCS8 data.");
            } else {
                byte[] bytes = section.getBase64DecodedBytes();
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

                KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                return privateKey;
            }
        } catch (Exception e) {
            unexpectedException = e;
        }
        throw JrCredentialError.newError(runtime, "Unexpected exception reading PKCS data: " + unexpectedException.getLocalizedMessage());
    }
}
