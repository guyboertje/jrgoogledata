package com.jrgoogledata;

import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.RubyClass;

import org.jruby.runtime.load.BasicLibraryService;

/**
 *
 * @author guy
 */
public class JrgoogledataService implements BasicLibraryService {

    @Override
    public boolean basicLoad(final Ruby runtime) throws IOException {

        RubyModule jrgd = runtime.defineModule("JrGoogleData");

        RubyClass gdSession = jrgd.defineClassUnder("Session", runtime.getObject(), JrSession.GDSESSION_ALLOCATOR);
        gdSession.defineAnnotatedMethods(JrSession.class);

        RubyClass gdWorkbook = jrgd.defineClassUnder("Workbook", runtime.getObject(), JrWorkbook.JRWORKBOOK_ALLOCATOR);
        gdWorkbook.defineAnnotatedMethods(JrWorkbook.class);

        RubyClass gdWorksheet = jrgd.defineClassUnder("Worksheet", runtime.getObject(), JrWorksheet.JRWORKSHEET_ALLOCATOR);
        gdWorksheet.defineAnnotatedMethods(JrWorksheet.class);

        RubyClass gdListQuery = jrgd.defineClassUnder("ListQuery", runtime.getObject(), JrListQuery.JRLISTQUERY_ALLOCATOR);
        gdListQuery.defineAnnotatedMethods(JrListQuery.class);

        RubyClass runtimeError = runtime.getRuntimeError();
        jrgd.defineClassUnder("CredentialError", runtimeError, runtimeError.getAllocator());

        jrgd.defineClassUnder("ReadError", runtimeError, runtimeError.getAllocator());

        jrgd.defineClassUnder("WriteError", runtimeError, runtimeError.getAllocator());

        return true;
    }
}
