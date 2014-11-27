package com.github.glomadrian.mpvcleanarchitecture.domain.repository;

import com.github.glomadrian.mpvcleanarchitecture.domain.model.MarvelCharacter;

import java.util.Collection;

/**
 * The domain model is different from others resources models, in each case the repository must map
 * the response to a basic model
 *
 * @author glomadrian
 */
public interface ResponseMapper<From> {

    Collection<MarvelCharacter> mapResponse(From response);
}
