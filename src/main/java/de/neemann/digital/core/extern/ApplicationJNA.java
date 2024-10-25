/*
 * Copyright (c) 2024 Ron Ren.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.core.extern;

import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.extern.handler.JNAInterface;
import de.neemann.digital.core.extern.handler.ProcessInterface;

import java.io.File;
import java.io.IOException;

/***
 *  verilator jna wrapper app
 */
public class ApplicationJNA implements Application {
    /***
     * verilator construction
     * @param label   the codes label
     * @param code    the code itself
     * @param inputs  the inputs expected by Digital
     * @param outputs the outputs expected by Digital
     * @param root    the projects main folder
     * @return JNA interface operator
     * @throws IOException
     */
    @Override
    public ProcessInterface start(String label, String code, PortDefinition inputs, PortDefinition outputs, File root) throws IOException {
        return new JNAInterface(code, inputs.getBits(), outputs.getBits());
    }

    @Override
    public boolean ensureConsistency(ElementAttributes attributes, File root) {
        return Application.super.ensureConsistency(attributes, root);
    }

    @Override
    public boolean checkSupported() {
        return Application.super.checkSupported();
    }

    @Override
    public String checkCode(String label, String code, PortDefinition inputs, PortDefinition outputs, File root) throws IOException {
        return Application.super.checkCode(label, code, inputs, outputs, root);
    }
}
