/*
 * Copyright (c) OmniFaces
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *     * Neither the name of OmniFaces nor the names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.omnifaces.vdldoc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ImpliedAttributeTest {

    @Test
    void newInstanceHasNullFields() {
        var attr = new ImpliedAttribute();

        assertThat(attr.getDisplayName()).isNull();
        assertThat(attr.getDescription()).isNull();
        assertThat(attr.getRequired()).isNull();
        assertThat(attr.getType()).isNull();
    }

    @Test
    void setAndGetDisplayName() {
        var attr = new ImpliedAttribute();
        attr.setDisplayName("myName");
        assertThat(attr.getDisplayName()).isEqualTo("myName");
    }

    @Test
    void setAndGetDescription() {
        var attr = new ImpliedAttribute();
        attr.setDescription("A description");
        assertThat(attr.getDescription()).isEqualTo("A description");
    }

    @Test
    void setAndGetRequired() {
        var attr = new ImpliedAttribute();
        attr.setRequired("true");
        assertThat(attr.getRequired()).isEqualTo("true");
    }

    @Test
    void setAndGetType() {
        var attr = new ImpliedAttribute();
        attr.setType("java.lang.Boolean");
        assertThat(attr.getType()).isEqualTo("java.lang.Boolean");
    }

    @Test
    void setAllFields() {
        var attr = new ImpliedAttribute();
        attr.setDisplayName("id");
        attr.setDescription("The component identifier.");
        attr.setRequired("false");
        attr.setType("java.lang.String");

        assertThat(attr.getDisplayName()).isEqualTo("id");
        assertThat(attr.getDescription()).isEqualTo("The component identifier.");
        assertThat(attr.getRequired()).isEqualTo("false");
        assertThat(attr.getType()).isEqualTo("java.lang.String");
    }

    @Test
    void fieldsCanBeOverwritten() {
        var attr = new ImpliedAttribute();
        attr.setDisplayName("first");
        attr.setDisplayName("second");
        assertThat(attr.getDisplayName()).isEqualTo("second");
    }

    @Test
    void fieldsCanBeSetToNull() {
        var attr = new ImpliedAttribute();
        attr.setDisplayName("name");
        attr.setDisplayName(null);
        assertThat(attr.getDisplayName()).isNull();
    }
}
