/*
 * Copyright (c) 2024 Ron Ren.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.core.extern.handler;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/***
 * common jna wrapper dll interface
 */
public interface CommonJNAInterface extends Library {
    /**
     * init loop
     */
    void cpuLoopInit();

    /***
     * get output
     * @param output out buffer
     * @param size  buffer size
     */
    void getOutput(Pointer output, int size);

    /***
     * set input
     * @param input input buffer
     * @param size buffer size
     */
    void setInput(Pointer input, int size);

    /***
     * stop run loop
     */
    void cpuLoopStop();
}
