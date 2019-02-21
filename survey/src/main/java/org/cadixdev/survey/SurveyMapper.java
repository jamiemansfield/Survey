/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.cadixdev.survey;

import com.google.gson.Gson;
import org.cadixdev.bombe.analysis.CachingInheritanceProvider;
import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.asm.analysis.ClassProviderInheritanceProvider;
import org.cadixdev.bombe.asm.jar.JarEntryRemappingTransformer;
import org.cadixdev.bombe.asm.jar.JarFileClassProvider;
import org.cadixdev.bombe.jar.Jars;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.asm.LorenzRemapper;
import org.cadixdev.lorenz.io.MappingFormat;
import org.cadixdev.survey.mapper.MapperEnvironment;
import org.cadixdev.survey.mapper.provider.MapperProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * A fluent interface for mapping and re-mapping with Survey.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class SurveyMapper {

    private final MappingSet mappings;

    public SurveyMapper(final MappingSet mappings) {
        this.mappings = mappings;
    }

    public SurveyMapper() {
        this(MappingSet.create());
    }

    /**
     * Loads mappings from the given path, using the given reader.
     *
     * @param mappingsPath The path to the mappings file
     * @param format The mapping format to use for reading the mappings file
     * @return {@code this}, for chaining
     */
    public SurveyMapper loadMappings(final Path mappingsPath, final MappingFormat format) {
        try {
            format.read(this.mappings, mappingsPath);
        }
        catch (final IOException ignored) {
        }
        return this;
    }

    public SurveyMapper saveMappings(final Path mappingsPath, final MappingFormat format) {
        try {
            format.write(this.mappings, mappingsPath);
        }
        catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Remaps the given input jar, with the loaded mappings, and saves it to
     * the given output path.
     *
     * @param input The input jar
     * @param output The output jar
     */
    public void remap(final Path input, final Path output) {
        try (final JarFile jarFile = new JarFile(input.toFile());
             final JarOutputStream jos = new JarOutputStream(Files.newOutputStream(output))) {
            final InheritanceProvider inheritance =
                    new CachingInheritanceProvider(new ClassProviderInheritanceProvider(new JarFileClassProvider(jarFile)));
            Jars.transform(jarFile, jos,
                    new JarEntryRemappingTransformer(
                            new LorenzRemapper(this.mappings, inheritance)
                    )
            );
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public SurveyMapper map(final Path jarPath, final MapperEnvironment env) {
        try (final JarFile jar = new JarFile(jarPath.toFile())) {
            env.run(jar);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public SurveyMapper map(final Path input, final Path configPath) {
        final Gson gson = MapperProviders.createGson(MapperProviders.REGISTRY, this.mappings);

        try (final BufferedReader reader = Files.newBufferedReader(configPath)) {
            final MapperEnvironment env = gson.fromJson(reader, MapperEnvironment.class);
            this.map(input, env);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

}
