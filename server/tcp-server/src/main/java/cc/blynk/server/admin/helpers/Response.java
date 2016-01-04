package cc.blynk.server.admin.helpers;

import cc.blynk.server.model.auth.User;
import cc.blynk.server.utils.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static cc.blynk.server.utils.ListUtils.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.12.15.
 */
public class Response extends DefaultFullHttpResponse {

    private static final String JSON = "application/json";
    private static final String PLAIN_TEXT = "text/plain";

    public Response(HttpVersion version, HttpResponseStatus status, String content, String contentType) {
        super(version, status, (content == null ? Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer(content, StandardCharsets.UTF_8)));
        headers().set(CONTENT_TYPE, contentType);
        headers().set(CONTENT_LENGTH, content().readableBytes());
    }

    public Response(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
    }
    
    public static Response ok() {
        return new Response(HTTP_1_1, OK);
    }

    public static Response notFound() {
        return new Response(HTTP_1_1, NOT_FOUND);
    }

    public static Response badRequest() {
        return new Response(HTTP_1_1, BAD_REQUEST);
    }

    public static Response badRequest(String message) {
        return new Response(HTTP_1_1, BAD_REQUEST, message, PLAIN_TEXT);
    }

    public static Response serverError() {
        return new Response(HTTP_1_1, INTERNAL_SERVER_ERROR);
    }

    public static Response serverError(String message) {
        return new Response(HTTP_1_1, INTERNAL_SERVER_ERROR, message, PLAIN_TEXT);
    }

    public static Response ok(String data) {
        return new Response(HTTP_1_1, OK, data, JSON);
    }

    public static Response ok(User user) {
        return ok(JsonParser.toJson(user));
    }

    public static Response ok(Collection<?> list, int page, int size) {
        return ok(JsonParser.toJson(subList(list, page, size)));
    }

    public static Response ok(Collection<?> list) {
        return ok(JsonParser.toJson(list));
    }

    public static Response appendTotalCountHeader(Response response, int count) {
        response.headers().set("X-Total-Count", count);
        response.headers().set("Access-Control-Expose-Headers", "x-total-count");
        return response;
    }
}