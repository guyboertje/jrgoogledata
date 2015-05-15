package com.jrgoogledata;

import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyObject;
import org.jruby.RubyString;
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
@JRubyClass(name = "JrGoogleData::Row", parent = "Object")
public class JrRow extends RubyObject {
    ListEntry _row;
    CustomElementCollection _elements;
    Set<String> _tags;
    Set<RubyString> _ruby_keys = new HashSet<>();

    public static final ObjectAllocator JRROW_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby ruby, RubyClass rc) {
            return new JrRow(ruby, rc);
        }
    };

    public JrRow(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    public JrRow(final Ruby runtime, ListEntry row) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("Row"));
        _row = row;
        _elements = row.getCustomElements();
        _tags = _elements.getTags();
        for (String tag : _tags) {
            _ruby_keys.add(RubyString.newUnicodeString(runtime, tag));
        }
    }

    @JRubyMethod(name = "new", meta = true)
    public static JrRow newInstance(IRubyObject self) {
        JrRow jr = (JrRow)((RubyClass) self).allocate();
        jr.callInit(Block.NULL_BLOCK);
        return jr;
    }

    @Override
    @JRubyMethod
    public IRubyObject inspect() {
        IRubyObject super_inspect = super.inspect();
        if (_row == null) {
            return super_inspect;
        }
        String part = super_inspect.toString();
        part = part.substring(0, part.length() - 3);
        StringBuilder str = new StringBuilder(part);
        str.append(" title: ");
        str.append(_row.getTitle().getPlainText());
        str.append(", content: { ");
        for (String tag : _tags) {
            str.append(tag).append(": ")
                    .append(_elements.getValue(tag))
                    .append("  ");
        }
        str.append("} >");
        return getRuntime().newString(str.toString());
    }

    @JRubyMethod
    public IRubyObject to_hash(ThreadContext context) {
        Ruby ruby = context.runtime;
        RubyHash hash = RubyHash.newHash(ruby);
        for (String tag : _tags) {
            RubyString key = RubyString.newUnicodeString(ruby, tag);
            String val = _elements.getValue(tag);
            hash.op_aset(context, key, RubyString.newUnicodeString(ruby, val));
        }
        return hash;
    }

    @JRubyMethod(required = 2)
    public IRubyObject modify(ThreadContext context, IRubyObject key, IRubyObject value) {
        String k = key.toString();
        if (_tags.contains(k)) {
            _elements.setValueLocal(k, value.toString());
        }
        return context.nil;
    }

    @JRubyMethod(required = 1)
    public IRubyObject merge(ThreadContext context, IRubyObject updates) {
        RubyHash u = (RubyHash)updates;
        for (RubyString key : _ruby_keys) {
            RubyString val = (RubyString) u.fetch(
                    context, key,
                    RubyString.newEmptyString(context.runtime),
                    Block.NULL_BLOCK);
            modify(context, key, val);
        }
        return context.nil;
    }

    @JRubyMethod
    public JrRow update(ThreadContext context) {
        String errorMessage = "Failed to update Row, reason unknown";
        try {
            _row.update();
            return this;
        }
        catch(IOException | ServiceException e) {
            errorMessage = e.getMessage();
        }
        throw JrReadError.newError(context.runtime, errorMessage);
    }

    public ListEntry getRow() {
        return _row;
    }
}
