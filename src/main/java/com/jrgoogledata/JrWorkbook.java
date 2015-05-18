package com.jrgoogledata;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.util.List;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author guy
 */
@JRubyClass(name = "JrGoogleData::Workbook", parent = "Object")
public class JrWorkbook extends RubyObject {
    private SpreadsheetEntry _entry;

    public static final ObjectAllocator JRWORKBOOK_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby ruby, RubyClass rc) {
            return new JrWorkbook(ruby, rc);
        }
    };

    public JrWorkbook(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    public JrWorkbook(final Ruby runtime, SpreadsheetEntry entry) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("Workbook"));
        _entry = entry;
    }

    @JRubyMethod(name = "new", meta = true)
    public static JrWorkbook newInstance(IRubyObject self) {
        JrWorkbook wb = (JrWorkbook)((RubyClass) self).allocate();
        wb.callInit(Block.NULL_BLOCK);
        return wb;
    }

    public JrWorkbook with_entry(SpreadsheetEntry entry) {
        _entry = entry;
        return this;
    }

    @JRubyMethod(name = "worksheet_by_title", required = 1)
    public JrWorksheet worksheetByTitle(ThreadContext context, IRubyObject title) {
        Ruby ruby = context.runtime;
        String _title = title.toString();
        String errorMessage = "Unable to find a worksheet with title: " + _title;
        try {
            for (WorksheetEntry entry : getWorksheets()) {
                String wsTitle = entry.getTitle().getPlainText();
                if (wsTitle.equals(_title)) {
                    return new JrWorksheet(ruby, entry);
                }
            }
        }
        catch (Exception e) {
            errorMessage = e.getLocalizedMessage();
        }
        throw JrReadError.newError(ruby, errorMessage);
    }

    @Override
    @JRubyMethod
    public IRubyObject inspect() {
        IRubyObject super_inspect = super.inspect();
        if (_entry == null) {
            return super_inspect;
        }
        String part = super_inspect.toString();
        part = part.substring(0, part.length() - 3);
        StringBuilder str = new StringBuilder(part);
        str.append(" id: ");
        str.append(_entry.getId());
        str.append(", worksheetfeed_url: ");
        str.append(_entry.getWorksheetFeedUrl().toString());
        str.append(" >");
        return getRuntime().newString( str.toString() );
    }

    private List<WorksheetEntry> getWorksheets() throws IOException, ServiceException {
        return _entry.getWorksheets();
    }
}
