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

package org.yar;


import javax.annotation.Nullable;
import java.util.List;

/**
 * TODO add type token or type literal
 * TODO see if we use directly the guice id...??? and they type literal
 * Date: 2/8/13
 * Time: 5:13 PM
 *
 * @author Romain Gilles
 * @since 1.0
 */
public interface Registry {

    <T> List<Supplier<T>> getAll(Class<T> type);

    @Nullable
    <T> Supplier<T> get(Class<T> type);

    <T> List<Supplier<T>> getAll(Id<T> id);

    @Nullable
    <T> Supplier<T> get(Id<T> id);

    <T> Registration<T> put(Id<T> id, Supplier<T> supplier);

    void remove(Registration<?> registration);

    <T> Registration<T> addWatcher(IdMatcher<T> watchedKey, Watcher<Supplier<T>> watcher);

    void removeWatcher(Registration<?> watcherRegistration);
}
