package com.acme.catchup.platform.news.application.internal.commandservices;

import com.acme.catchup.platform.news.domain.model.aggregates.FavoriteSource;
import com.acme.catchup.platform.news.domain.model.commands.CreateFavoriteSourceCommand;
import com.acme.catchup.platform.news.domain.services.FavoriteSourceCommandService;
import com.acme.catchup.platform.news.infrastructure.persistence.jpa.FavoriteSourceRepository;
import static com.acme.catchup.platform.news.domain.model.aggregates.FavoriteSource.NEWS_API_KEY_SOURCE_ID_UNIQUE_CONSTRAINT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * FavoriteSourceCommandService Implementation
 *
 * @summary
 * Implementation of the FavoriteSourceCommandService interface.
 * It is responsible for handling favorite source commands.
 *
 * @since 1.0
 */
@Service
public class FavoriteSourceCommandServiceImpl implements FavoriteSourceCommandService {
    private static final String DUPLICATE_FAVORITE_SOURCE_CONSTRAINT = NEWS_API_KEY_SOURCE_ID_UNIQUE_CONSTRAINT;
    private final FavoriteSourceRepository favoriteSourceRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteSourceCommandServiceImpl.class);
    private final MessageSource messageSource;

    public FavoriteSourceCommandServiceImpl(FavoriteSourceRepository favoriteSourceRepository, MessageSource messageSource) {
        this.favoriteSourceRepository = favoriteSourceRepository;
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    @Transactional
    public Optional<FavoriteSource> handle(CreateFavoriteSourceCommand command) {
        try {
            var favoriteSource = new FavoriteSource(command);
            var createdFavoriteSource = favoriteSourceRepository.save(favoriteSource);
            LOGGER.info("Favorite source created: newsApiKey={}, sourceId={}, id={}, createdAt={}, updatedAt={}",
                    command.newsApiKey(),
                    command.sourceId(),
                    createdFavoriteSource.getId(),
                    createdFavoriteSource.getCreatedAt(),
                    createdFavoriteSource.getUpdatedAt());
            return Optional.of(createdFavoriteSource);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateFavoriteSourceViolation(exception)) {
                // Invariant violation: Duplicate favorite source
                LOGGER.warn(messageSource.getMessage("favorite.source.error.duplicate", null, LocaleContextHolder.getLocale()));
                return Optional.empty();
            }
            throw exception;
        }
    }

    private boolean isDuplicateFavoriteSourceViolation(DataIntegrityViolationException exception) {
        Throwable violationCause = exception;
        while (violationCause != null) {
            String message = violationCause.getMessage();
            if (message != null && message.contains(DUPLICATE_FAVORITE_SOURCE_CONSTRAINT)) {
                return true;
            }
            violationCause = violationCause.getCause();
        }
        return false;
    }
}
