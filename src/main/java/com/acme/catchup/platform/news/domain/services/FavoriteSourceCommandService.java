package com.acme.catchup.platform.news.domain.services;

import com.acme.catchup.platform.news.domain.model.aggregates.FavoriteSource;
import com.acme.catchup.platform.news.domain.model.commands.CreateFavoriteSourceCommand;

import java.util.Optional;

/**
 * Domain service defining the contract for favorite source command operations.
 * Declares the command handler methods for creating favorite sources.
 * Implementations are application services that abstract away infrastructure concerns
 * while maintaining focus on domain invariants and business rules.
 */
public interface FavoriteSourceCommandService {
    /**
     * Handles creation of a favorite source.
     *
     * @param command create a command containing the news API key and source ID
     * @return the created favorite source, or empty when the request represents a duplicate favorite source
     *
     * @throws IllegalArgumentException If newsApiKey or source ID is null or empty
     * @see CreateFavoriteSourceCommand
     */
    Optional<FavoriteSource> handle(CreateFavoriteSourceCommand command);
}
