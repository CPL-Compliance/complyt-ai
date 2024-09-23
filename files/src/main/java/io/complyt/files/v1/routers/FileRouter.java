package io.complyt.files.v1.routers;

import io.complyt.files.v1.api_info.DeleteFilesApiInfo;
import io.complyt.files.v1.api_info.GetFilesApiInfo;
import io.complyt.files.v1.api_info.GetLinkApiInfo;
import io.complyt.files.v1.api_info.SaveFilesApiInfo;
import io.complyt.files.v1.handlers.FileHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class FileRouter {
    public static final String BASE_URL = "/v1/files_old";
    public static final String COMPLYT_FILE_BASE_URL = "/v1/files";

    @Bean
    @GetLinkApiInfo
    public RouterFunction<ServerResponse> getfileLinkRouterFunction(@NonNull final FileHandler fileHandler) {
        RequestPredicate getFileLinkRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getFileLinkRoute, fileHandler::get);
    }

    @Bean
    @GetFilesApiInfo
    public RouterFunction<ServerResponse> getListOfFilesInTenant(@NonNull final FileHandler fileHandler) {
        RequestPredicate getListOfFilesInTenant = RequestPredicates
                .GET(COMPLYT_FILE_BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getListOfFilesInTenant, fileHandler::getListOfFileInTenant);
    }

    @Bean
    @SaveFilesApiInfo
    public RouterFunction<ServerResponse> saveFile(@NonNull final FileHandler fileHandler) {
        RequestPredicate saveFile = RequestPredicates
                .PUT(COMPLYT_FILE_BASE_URL)
                .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA));

        return RouterFunctions.route(saveFile, fileHandler::saveFile);
    }

    @Bean
    @GetFilesApiInfo
    public RouterFunction<ServerResponse> getFileWithSignedLink(@NonNull final FileHandler fileHandler) {
        RequestPredicate getFileWithSignedLink = RequestPredicates.GET(COMPLYT_FILE_BASE_URL + "/{complytId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getFileWithSignedLink, fileHandler::getFileWithSignedLink);
    }

    @Bean
    @DeleteFilesApiInfo
    public RouterFunction<ServerResponse> markAsDeletedFile(@NonNull final FileHandler fileHandler) {
        RequestPredicate markAsDeletedFile = RequestPredicates.DELETE(COMPLYT_FILE_BASE_URL + "/{complytId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(markAsDeletedFile, fileHandler::markAsDeleted);
    }

}
