/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.cadixdev.survey.config.mapper.intermediary.provider;

import org.cadixdev.survey.config.mapper.intermediary.FieldIntemediaryMapperConfigDeserialiser;
import org.cadixdev.survey.config.mapper.provider.SimpleMapperProvider;
import org.cadixdev.survey.context.SurveyContext;
import org.cadixdev.survey.mapper.intermediary.FieldIntemediaryMapper;

/**
 * The mapper provider for the field intermediary mapper.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class FieldIntermediaryMapperProvider
        extends SimpleMapperProvider<FieldIntemediaryMapper, FieldIntemediaryMapper.Config> {

    private static final String ID = "intermediary_fields";

    public FieldIntermediaryMapperProvider() {
        super(ID, FieldIntemediaryMapper.Config.class, FieldIntemediaryMapperConfigDeserialiser.INSTANCE);
    }

    @Override
    public FieldIntemediaryMapper create(final SurveyContext ctx, final FieldIntemediaryMapper.Config config) {
        return new FieldIntemediaryMapper(ctx, config);
    }

}
