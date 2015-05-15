package com.jrgoogledata;


import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyArray;
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
@JRubyClass(name = "JrGoogleData::Worksheet", parent = "Object")
public class JrWorksheet extends RubyObject {
    WorksheetEntry _entry;

    public static final ObjectAllocator JRWORKSHEET_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby ruby, RubyClass rc) {
            return new JrWorksheet(ruby, rc);
        }
    };

    public JrWorksheet(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    public JrWorksheet(final Ruby runtime, WorksheetEntry entry) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("Worksheet"));
        _entry = entry;
    }

    @JRubyMethod(name = "new", meta = true)
    public static JrWorksheet newInstance(IRubyObject self) {
        JrWorksheet ws = (JrWorksheet)((RubyClass) self).allocate();
        ws.callInit(Block.NULL_BLOCK);
        return ws;
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
        str.append(" cell_feed_url: ");
        str.append(_entry.getCellFeedUrl().toString());
        str.append(", list_feed_url: ");
        str.append(_entry.getListFeedUrl().toString());
        str.append(" >");
        return getRuntime().newString(str.toString());
    }

    @JRubyMethod(name = "new_list_query")
    public IRubyObject newListQuery(ThreadContext context) {
        return new JrListQuery(
                context.runtime,
                _entry.getListFeedUrl()
        );
    }
    
    @JRubyMethod
    public IRubyObject insert_row(ThreadContext context, IRubyObject row) {
        String errorMessage = "Unknown error occured while inserting new Row";
        try {
            JrRow jr = (JrRow) row;
            ListEntry entry = jr.getRow();
            getService().insert(_entry.getListFeedUrl(), entry);
            return context.nil;
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        throw JrWriteError.newError(context.runtime, errorMessage);
    }

    @JRubyMethod(required = 1)
    public RubyArray get_rows(ThreadContext context, IRubyObject query) {

        Ruby ruby = context.runtime;
        String errorMessage = "Unknown error occured while getting Rows";
        RubyArray array = ruby.newArray();
        try {
            ListQuery q = ((JrListQuery)query).getQuery();
            ListFeed feed  = getService().getFeed(q.getUrl(), ListFeed.class);
            for (ListEntry row: feed.getEntries ()) {
              array.append(new JrRow(ruby, row));
            }
            return array;
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
        }
        throw JrReadError.newError(ruby, errorMessage);
    }

    private SpreadsheetService getService() {
        return (SpreadsheetService)_entry.getService();
    }
}
