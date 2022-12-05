//   +---------------------------------------------------------------+
//   | etaoin-shrdlu: LISP interpreter for the SHRDLU project        |
//   |                                                               |
//   | Original source code is published in github resository:       |
//   | https://github.com/pcerman/etaoin-shrdlu.                     |
//   |                                                               |
//   | Copyright (c) 2021 Peter Cerman (https://github.com/pcerman)  |
//   |                                                               |
//   | This source code is released under Mozilla Public License 2.0 |
//   +---------------------------------------------------------------+

package etaoin.data;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.Reader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Port extends Value {

    public static enum PortType { INP, OUT, APP }

    private final PortType portType;
    private final File file;

    private Reader reader;
    private OutputStreamWriter writer;

    public Port(PortType type, File file, FileReader reader) {
        this.portType = type;
        this.file = file;
        this.reader = new Reader(reader);
        this.writer = null;
    }

    public Port(PortType type, File file, FileWriter writer) {
        this.portType = type;
        this.file = file;
        this.reader = null;
        this.writer = writer;
    }

    public Port(PortType type, InputStreamReader reader) {
        this.portType = type;
        this.file = null;
        this.reader = new Reader(reader);
        this.writer = null;
    }

    public Port(PortType type, OutputStreamWriter writer) {
        this.portType = type;
        this.file = null;
        this.reader = null;
        this.writer = writer;
    }

    public PortType getPortType() {
        return portType;
    }

    public String getFilename() {
        return file != null ? file.getPath() : "";
    }

    public boolean close() {
        try {
            if (reader != null) {
                reader.close();
                reader = null;
                return true;
            }

            if (writer != null) {
                writer.close();
                writer = null;
                return true;
            }
        }
        catch (IOException ex) { }

        return false;
    }

    public boolean isClosed() {
        return reader == null && writer == null;
    }

    public Value read(Interpreter in, Environment env) throws LispException {
        try {
            if (reader != null)
                return in.read(reader, env);
        }
        catch (LispException ex) {
            throw ex;
        }
        catch (Exception ex) { }

        return null;
    }

    public int read_char() {
        try {
            if (reader != null)
                return reader.read_char();
        }
        catch (Exception ex) { }

        return -1;
    }

    public int peek_char() {
        try {
            if (reader != null)
                return reader.peek_char();
        }
        catch (Exception ex) { }

        return -1;
    }

    public String read_line() {
        try {
            if (reader != null)
                return reader.read_line();
        }
        catch (Exception ex) { }

        return null;
    }

    @Override
    public Type getType() {
        return Type.PORT;
    }

    public int printf(String fmt, Object... args) {
        String str = String.format(fmt, args);
        return print(str);
    }

    public int print(String str) {
        try {
            if (writer != null) {
                writer.write(str);
                return str.length();
            }
        } catch (IOException ex) { }
        return -1;
    }

    @Override
    protected void pr_str(StringBuilder builder, boolean readably) {
        if (isClosed()) {
            String msg = String.format("<PORT closed 0x%X>", this.hashCode());
            builder.append(msg);
        } else {
            String msg = String.format("<PORT-%s 0x%X>", portType.name(), this.hashCode());
            builder.append(msg);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        }
        finally {
            super.finalize();
        }
    }

    public static Port openInp(File file) {
        try {
            FileReader reader = new FileReader(file);
            return new Port(PortType.INP, file, reader);
        }
        catch (FileNotFoundException ex) { }

        return null;
    }

    public static Port openOut(File file, boolean append) {
        try {
            PortType outType = append ? PortType.APP : PortType.OUT;
            FileWriter writer = new FileWriter(file, append);
            return new Port(outType, file, writer);
        }
        catch (IOException ex) { }

        return null;
    }
}
