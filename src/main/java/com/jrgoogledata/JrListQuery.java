package com.jrgoogledata;

import com.google.gdata.client.spreadsheet.ListQuery;
import java.net.URL;
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

/**
 *
 * @author guy
 */
@JRubyClass(name = "JrGoogleData::ListQuery", parent = "Object")
public class JrListQuery extends RubyObject {
    ListQuery _query;
    RubyArray _columnNames;


    public static final ObjectAllocator JRLISTQUERY_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby ruby, RubyClass rc) {
            return new JrListQuery(ruby, rc);
        }
    };

    public JrListQuery(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    public JrListQuery(final Ruby runtime, URL feed) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("ListQuery"));
        _query = new ListQuery(feed);
    }

    public JrListQuery(final Ruby runtime, RubyClass rubyClass, URL feed) {
        super(runtime, rubyClass);
        _query = new ListQuery(feed);
    }

    @JRubyMethod(name = "new", meta = true)
    public static JrListQuery newInstance(IRubyObject self) {
        JrListQuery lq = (JrListQuery)((RubyClass) self).allocate();
        lq.callInit(Block.NULL_BLOCK);
        return lq;
    }

    public JrListQuery withColumnNames(ThreadContext context, IRubyObject names) {
        _columnNames = (RubyArray)names;
        return this;
    }

    @JRubyMethod(required = 1)
    public JrListQuery with_start_index(ThreadContext context, IRubyObject n) {
        int i = RubyNumeric.num2int(n);
        _query.setStartIndex(i);
        return this;
    }

    @JRubyMethod(required = 1)
    public JrListQuery with_max_results(ThreadContext context, IRubyObject n) {
        int i = RubyNumeric.num2int(n);
        _query.setMaxResults(i);
        return this;
    }

    @JRubyMethod(required = 1)
    public JrListQuery with_spreadsheet_query(ThreadContext context, IRubyObject ssq) {
        _query.setSpreadsheetQuery(ssq.toString());
        return this;
    }

    @JRubyMethod(required = 1)
    public JrListQuery with_full_text_query(ThreadContext context, IRubyObject ftq) {
        _query.setFullTextQuery(ftq.toString());
        return this;
    }

    public ListQuery getQuery() {
        return _query;
    }
}
