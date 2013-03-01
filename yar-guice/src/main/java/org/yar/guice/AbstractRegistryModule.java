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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import java.lang.reflect.Method;

import static org.yar.guice.RegistrationBindingBuilderImpl.bindRegistration;

/**
 * TODO comment
 * Date: 2/28/13
 * Time: 11:36 PM
 *
 * @author Romain Gilles
 */
public abstract class AbstractRegistryModule extends AbstractModule {

    @Override
    protected final void configure() {
        doBeforeConfiguration();
        configureRegistry();
        bindProviderMethods();
    }

    void doBeforeConfiguration() {
    }

    private void bindProviderMethods() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Register.class) && method.isAnnotationPresent(Provides.class)) {
                bindRegistration(binder(), Key.get(method.getGenericReturnType()));
            }
        }

    }

    /**
     * TODO
     */
    protected abstract void configureRegistry();

    protected <T> RegistrationLinkedBindingBuilder<T> register(Key<T> key) {
        return new RegistrationBindingBuilderImpl<>(binder(), key);
    }

    protected <T> RegistrationAnnotatedBindingBuilder<T> register(TypeLiteral<T> typeLiteral) {
        return new RegistrationBindingBuilderImpl<>(binder(), typeLiteral);
    }

    // TODO cannot be scoped
    protected <T> RegistrationAnnotatedBindingBuilder<T> register(Class<T> type) {
        return new RegistrationBindingBuilderImpl<>(binder(), type);
    }

    @Override
    protected <T> RegistryLinkedBindingBuilder<T> bind(Key<T> key) {
        RegistryAnnotatedBindingBuilder<T> registryAnnotatedBindingBuilder = new RegistryBindingBuilder<>(binder(), key);
        return registryAnnotatedBindingBuilder;
    }

    @Override
    protected <T> RegistryAnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        RegistryAnnotatedBindingBuilder<T> registryAnnotatedBindingBuilder = new RegistryBindingBuilder<>(binder(), typeLiteral);
        return registryAnnotatedBindingBuilder;
    }

    @Override
    protected <T> RegistryAnnotatedBindingBuilder<T> bind(Class<T> clazz) {
        RegistryAnnotatedBindingBuilder<T> registryAnnotatedBindingBuilder = new RegistryBindingBuilder<>(binder(), clazz);
        return registryAnnotatedBindingBuilder;
    }
}
