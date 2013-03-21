/*
 * Copyright 2013 Romain Gilles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yar.guice;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.yar.Id;
import org.yar.Registration;
import org.yar.Registry;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.collect.Lists.transform;

/**
 * TODO comment
 * Date: 2/10/13
 * Time: 10:53 PM
 *
 * @author Romain Gilles
 */
public class RegistrationBindingHandler implements RegistrationHandler {
    private final Injector injector;
    private final Registry registry;
    private final List<Registration<?>> registrations;
    @Inject
    public RegistrationBindingHandler(Injector injector, Registry registry) {
        this.injector = injector;
        this.registry = registry;
        registrations = registerBindings();
    }

    private List<Registration<?>> registerBindings() {
        ImmutableList.Builder<Registration<?>> registrationsBuilder = ImmutableList.builder();
        for (Binding<GuiceRegistration> registrationBinding : injector.findBindingsByType(TypeLiteral.get(GuiceRegistration.class))) {
            Key<?> key = registrationBinding.getProvider().get().key();
            registrationsBuilder.add(putRegistrationToRegistry(key));
        }
        return registrationsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private Registration<?> putRegistrationToRegistry(Key<?> key) {
        return registry.put(GuiceId.of(key), new GuiceSupplier(injector.getProvider(key)));
    }

    @Override
    public List<Id<?>> registeredKeys() {
        return transform(registrations, new Function<Registration<?>, Id<?>>() {
            @Nullable
            @Override
            public Id<?> apply(@Nullable Registration<?> registration) {
                if (registration == null) {
                    throw new NullPointerException("registration");
                }
                return registration.id();
            }
        });
    }

    @Override
    public void clear() {
        for (Registration<?> registration : registrations) {
            registry.remove(registration);
        }
    }
}
