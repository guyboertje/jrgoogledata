package com.jrgoogledata;

import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
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
    boolean _empty;
    boolean _insertOnly;
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
        _insertOnly = false;
        setupTags(runtime, _elements.getTags());
    }

    public JrRow(final Ruby runtime, ListEntry row, Set<String> tags) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("Row"));
        _row = row;
        _elements = row.getCustomElements();
        _insertOnly = true;
        setupTags(runtime, tags);
    }

    private void setupTags(Ruby runtime, Set<String> tags) {
        _tags = tags;
        _empty = _tags.isEmpty();
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
        setLocal(key.toString(), value.toString());
        return context.nil;
    }

    private void setLocal(String key, String val) {
        if (_tags.contains(key)) {
            _elements.setValueLocal(key, val);
        }
    }

    @JRubyMethod(required = 1)
    public IRubyObject merge(ThreadContext context, IRubyObject updates) {
        RubyHash u = (RubyHash)updates;
        for (RubyString key : _ruby_keys) {
            RubyString val = (RubyString) u.fetch(
                    context, key,
                    RubyString.newEmptyString(context.runtime),
                    Block.NULL_BLOCK);
            setLocal(key.toString(), val.toString());
        }
        return context.nil;
    }

    @JRubyMethod
    public JrRow update(ThreadContext context) {
        if (_insertOnly) {
            throw JrWriteError.newError(context.runtime, "This Row is insert only, it does not exist on the sheet yet");
        }
        String errorMessage = "Failed to update Row, reason unknown";
        try {
            _row.update();
            return this;
        }
        catch(Exception e) {
            errorMessage = e.getMessage();
        }
        throw JrWriteError.newError(context.runtime, errorMessage);
    }

    public ListEntry getRow() {
        return _row;
    }
}
