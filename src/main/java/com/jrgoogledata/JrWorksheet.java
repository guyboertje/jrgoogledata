package com.jrgoogledata;


import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.util.Set;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import java.net.URL;
import java.util.Iterator;
import org.jruby.RubyString;

/**
 *
 * @author guy
 */
@JRubyClass(name = "JrGoogleData::Worksheet", parent = "Object")
public class JrWorksheet extends RubyObject {
    WorksheetEntry _entry;
    URL _listFeedUrl;
    Set<String> _columnNames;
    RubyArray _rubyNames;

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
        super(runtime, runtime.getModule("JrGoogleData")
                .getClass("Worksheet"));

        _entry = entry;
        _listFeedUrl = _entry.getListFeedUrl();
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
        Ruby ruby = getRuntime();
        String part = super_inspect.toString();
        part = part.substring(0, part.length() - 3);
        StringBuilder str = new StringBuilder(part);
        str.append(" cell_feed_url: ");
        str.append(_entry.getCellFeedUrl().toString());
        str.append(", list_feed_url: ");
        str.append(_listFeedUrl.toString());
        str.append(", columns: ");
        str.append(rubyNames(ruby).inspect().toString());
        str.append(" >");
        return ruby.newString(str.toString());
    }

    @JRubyMethod
    public IRubyObject column_names(ThreadContext context) {
        return rubyNames(context.runtime);
    }

    @JRubyMethod(name = "new_list_query")
    public IRubyObject newListQuery(ThreadContext context) {
        return new JrListQuery(context.runtime,_listFeedUrl).withColumnNames(context, rubyNames(context.runtime));
    }

    @JRubyMethod(required = 1)
    public IRubyObject update_rows(ThreadContext context, IRubyObject arrayOfRows) {
        RubyArray y = arrayOfRows.convertToArray();
        for (Iterator iterator = y.iterator(); iterator.hasNext();) {
            IRubyObject row = (IRubyObject)iterator.next();
            if (row instanceof JrRow) {
                JrRow r = (JrRow)row;
                r.update(context);
            }
        }
        return context.nil;
    }

    @JRubyMethod(required = 1)
    public IRubyObject add_rows(ThreadContext context, IRubyObject arrayOfRows) {
        RubyArray y = arrayOfRows.convertToArray();
        for (Iterator iterator = y.iterator(); iterator.hasNext();) {
            IRubyObject row = (IRubyObject)iterator.next();
            if (row instanceof JrRow) {
                add_row(context, row);
            }
        }
        return context.nil;
    }

    @JRubyMethod(required = 1)
    public IRubyObject add_row(ThreadContext context, IRubyObject row) {
        String errorMessage = "Unknown error occured while inserting new Row";
        try {
            JrRow jr = (JrRow) row;
            ListEntry entry = jr.getRow();
            getService().insert(_listFeedUrl, entry);
            return context.nil;
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        throw JrWriteError.newError(context.runtime, errorMessage);
    }

    @JRubyMethod(required = 1)
    public IRubyObject new_rows(ThreadContext context, IRubyObject amount) {
        Ruby ruby = context.runtime;
        int amt = RubyNumeric.num2int(amount);
        IRubyObject[] vals = new IRubyObject[amt];
        for (int i = 0; i < amt; i++) {
            vals[i] = newRow(ruby);
        }
        return RubyArray.newArrayNoCopyLight(ruby, vals);
    }

    @JRubyMethod
    public IRubyObject new_row(ThreadContext context) {
        return newRow(context.runtime);
    }

    private JrRow newRow(Ruby ruby) {
        String errorMessage = "Unknown error occured building a new Row";
        try {
          return new JrRow(ruby, _newRow(), columnNames());
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
        }
        throw JrReadError.newError(ruby, errorMessage);
    }

    private ListEntry _newRow() throws IOException, ServiceException{
        return getStdListFeed().createEntry();
    }

    @JRubyMethod(required = 1)
    public RubyArray fetch_rows(ThreadContext context, IRubyObject query) {

        Ruby ruby = context.runtime;
        String errorMessage = "Unknown error occured while getting Rows";
        RubyArray array = ruby.newArray();
        try {
            ListQuery q = ((JrListQuery)query).getQuery();
            ListFeed feed  = getListFeedFrom(q);
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

    private ListFeed getStdListFeed() throws IOException, ServiceException {
        return getListFeedFrom(_listFeedUrl);
    }

    private ListFeed getListFeedFrom(URL url) throws IOException, ServiceException {
        return getService().getFeed(url, ListFeed.class);
    }

    private ListFeed getListFeedFrom(ListQuery q) throws IOException, ServiceException {
        return getService().query(q, ListFeed.class);
    }

    private SpreadsheetService getService() {
        return (SpreadsheetService)_entry.getService();
    }

    private Set<String> columnNames() {
        if (_columnNames == null) {
            try {
                fillColumnNames();
            } catch (Exception e) {
                throw new JrIOError(e.getLocalizedMessage());
            }
        }
        return _columnNames;
    }

    private void fillColumnNames() throws IOException, ServiceException{
        ListQuery q = new ListQuery(_listFeedUrl);
        q.setStartIndex(1);
        q.setMaxResults(1);
        ListFeed results  = getListFeedFrom(q);
        ListEntry entry = results.getEntries().get(0);
        _columnNames = entry.getCustomElements().getTags();
    }

    private RubyArray rubyNames(Ruby ruby) {
        if (_rubyNames == null) {
            fillRubyNames(ruby);
        }
        return _rubyNames;
    }

    private void fillRubyNames(Ruby ruby) {
        Set<String> names = columnNames();
        _rubyNames = RubyArray.newArray(ruby, names.size());
        for (String name : names) {
            _rubyNames.append(RubyString.newUnicodeString(ruby, name));
        }
    }
}
