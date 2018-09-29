/*
 * Copyright (c) 2018, Jamie Mansfield <https://jamiemansfield.me/>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.jamiemansfield.survey.test.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.jar.JarServiceProviderConfigurationEntry;
import me.jamiemansfield.bombe.jar.ServiceProviderConfiguration;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.survey.jar.ServiceProviderConfigurationRemappingTransformer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Unit tests pertaining to {@link ServiceProviderConfigurationRemappingTransformer}.
 */
public final class ServiceProviderConfigurationRemappingTransformerTests {

    private static final MappingSet MAPPINGS = MappingSet.create();
    private static final ServiceProviderConfigurationRemappingTransformer TRANSFORMER =
            new ServiceProviderConfigurationRemappingTransformer(MAPPINGS);

    static {
        MAPPINGS.getOrCreateTopLevelClassMapping("demo/Demo")
                .setDeobfuscatedName("demo/RemappedDemo");
        MAPPINGS.getOrCreateTopLevelClassMapping("demo/DemoImpl")
                .setDeobfuscatedName("demo/RemappedDemoImpl");
        MAPPINGS.getOrCreateTopLevelClassMapping("demo/DemoAltImpl")
                .setDeobfuscatedName("demo/RemappedDemoAltImpl");
    }

    @Test
    public void remapsConfig() {
        final ServiceProviderConfiguration obfConfig = new ServiceProviderConfiguration("demo.Demo", Arrays.asList(
                "demo.DemoImpl",
                "demo.DemoAltImpl"
        ));
        final ServiceProviderConfiguration deobfConfig =
                TRANSFORMER.transform(new JarServiceProviderConfigurationEntry(obfConfig)).getConfig();
        assertEquals("demo.RemappedDemo", deobfConfig.getService());
        assertEquals(2, deobfConfig.getProviders().size());
        assertTrue(deobfConfig.getProviders().contains("demo.RemappedDemoImpl"), "Provider not present!");
        assertTrue(deobfConfig.getProviders().contains("demo.RemappedDemoAltImpl"), "Provider not present!");
    }

}