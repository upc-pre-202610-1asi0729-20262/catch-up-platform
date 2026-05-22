package com.acme.catchup.platform.news.application.internal.queryservices;

import com.acme.catchup.platform.news.domain.model.aggregates.FavoriteSource;
import com.acme.catchup.platform.news.domain.model.queries.GetAllFavoriteSourcesByNewsApiKeyQuery;
import com.acme.catchup.platform.news.domain.model.queries.GetFavoriteSourceByIdQuery;
import com.acme.catchup.platform.news.domain.model.queries.GetFavoriteSourceByNewsApiKeyAndSourceIdQuery;
import com.acme.catchup.platform.news.domain.services.FavoriteSourceQueryService;
import com.acme.catchup.platform.news.infrastructure.persistence.jpa.FavoriteSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service for favorite source query operations.
 * Contains query handler methods that translate domain query specifications
 * into persistence operations, returning domain models from storage.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class FavoriteSourceQueryServiceImpl implements FavoriteSourceQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteSourceQueryServiceImpl.class);
    private final FavoriteSourceRepository favoriteSourceRepository;

    public FavoriteSourceQueryServiceImpl(FavoriteSourceRepository favoriteSourceRepository) {
        this.favoriteSourceRepository = favoriteSourceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FavoriteSource> handle(GetAllFavoriteSourcesByNewsApiKeyQuery query) {
        LOGGER.debug("Querying all favorite sources for newsApiKey={}", query.newsApiKey());
        var results = favoriteSourceRepository.findAllByNewsApiKey(query.newsApiKey());
        LOGGER.debug("Found {} favorite source(s) for newsApiKey={}", results.size(), query.newsApiKey());
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FavoriteSource> handle(GetFavoriteSourceByIdQuery query) {
        LOGGER.debug("Querying favorite source by id={}", query.id());
        var result = favoriteSourceRepository.findById(query.id());
        if (result.isEmpty()) LOGGER.debug("No favorite source found for id={}", query.id());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FavoriteSource> handle(GetFavoriteSourceByNewsApiKeyAndSourceIdQuery query) {
        LOGGER.debug("Querying favorite source by newsApiKey={}, sourceId={}", query.newsApiKey(), query.sourceId());
        var result = favoriteSourceRepository.findByNewsApiKeyAndSourceId(query.newsApiKey(), query.sourceId());
        if (result.isEmpty()) LOGGER.debug("No favorite source found for newsApiKey={}, sourceId={}", query.newsApiKey(), query.sourceId());
        return result;
    }
}
