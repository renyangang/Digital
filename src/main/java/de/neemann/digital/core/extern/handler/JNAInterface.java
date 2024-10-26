/*
 * Copyright (c) 2024 Ron Ren.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.core.extern.handler;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

/**
 * JNAInterface for run verilator dll with jna
 */
public class JNAInterface implements ProcessInterface {
    private BitSet inSet;
    private Memory inputBuffer;
    private Memory outputBuffer;
    private BitSet outSet;
    private CommonJNAInterface dllInterface;
    private int inputBitLength;
    private int outputBitLength;
    private int inputByteLength;
    private int outputByteLength;
    private boolean isInit = false;

    /***
     * construction
     * @param dllPath verilator dll wrapper path
     * @param inputLength input sigs length
     * @param outputLength output sigs length
     */
    public JNAInterface(String dllPath, int inputLength, int outputLength) {
        Path dPath = Paths.get(dllPath);
        NativeLibrary.addSearchPath(String.valueOf(dPath.getFileName()), String.valueOf(dPath.getParent()));
        dllInterface = Native.load(String.valueOf(dPath.getFileName()), CommonJNAInterface.class);
        inSet = new BitSet(inputLength+1);
        inSet.set(inputLength, true); // make toByteArray can get full length bytes array.
        this.inputBitLength = inputLength;
        this.outputBitLength = outputLength;
        this.inputByteLength = (this.inputBitLength+7)/8;
        this.outputByteLength = (this.outputBitLength+7)/8;
        this.inputBuffer = new Memory(this.inputByteLength);
        this.outputBuffer = new Memory(this.outputByteLength);
        this.init();
    }

    private void init() {
        if (!this.isInit) {
            this.dllInterface.cpuLoopInit();
            this.isInit = true;
        }
    }

    @Override
    public void writeValues(ObservableValues values) throws IOException {
        this.init();
        int pos = 0;
        for (ObservableValue v : values) {
            final int bits = v.getBits();
            final long value = v.getValue();
            final long highZ = v.getHighZ();
            long mask = 1;
            for (int i = 0; i < bits; i++) {
                this.inSet.set(pos, (value & mask) != 0);
                mask <<= 1;
                pos++;
            }
        }
        this.inputBuffer.write(0, this.inSet.toByteArray(), 0, this.inputByteLength);
        this.dllInterface.setInput(this.inputBuffer, this.inputByteLength);
    }

    @Override
    public void readValues(ObservableValues values) throws IOException {
        this.init();
        this.dllInterface.getOutput(this.outputBuffer, this.outputByteLength);
        this.outSet = BitSet.valueOf(this.outputBuffer.getByteArray(0, this.outputByteLength));
        int pos = 0;
        for (ObservableValue v : values) {
            final int bits = v.getBits();
            int value = 0;
            for (int i=0; i<bits; i++) {
                boolean bSet = this.outSet.get(pos);
                if (bSet) {
                    value |= (1 << i);
                } else {
                    value &= (~(1 << i));
                }
                pos++;
            }
            v.set(value, 0);
        }
    }

    @Override
    public void close() throws IOException {
        this.dllInterface.cpuLoopStop();
        this.isInit = false;
    }
}
