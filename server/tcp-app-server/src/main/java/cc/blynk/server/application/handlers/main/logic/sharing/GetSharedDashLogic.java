package cc.blynk.server.application.handlers.main.logic.sharing;

import cc.blynk.server.core.dao.SharedTokenValue;
import cc.blynk.server.core.dao.TokenManager;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.exceptions.InvalidTokenException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.JsonParser;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import static cc.blynk.server.core.protocol.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetSharedDashLogic {

    private final TokenManager tokenManager;

    public GetSharedDashLogic(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    private static Integer getSharedDashId(Map<Integer, String> sharedTokens, String token) {
        for (Map.Entry<Integer, String> entry : sharedTokens.entrySet()) {
            if (entry.getValue().equals(token)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void messageReceived(ChannelHandlerContext ctx, StringMessage message) {
        String token = message.body;

        SharedTokenValue tokenValue = tokenManager.getUserBySharedToken(token);

        if (tokenValue == null) {
            throw new InvalidTokenException("Illegal sharing token. No user with those shared token.", message.id);
        }

        User userThatShared = tokenValue.user;

        Integer dashId = getSharedDashId(userThatShared.dashShareTokens, token);

        if (dashId == null) {
            throw new InvalidTokenException("Illegal sharing token. User has not token. Could happen only in rare cases.", message.id);
        }

        DashBoard dashBoard = userThatShared.profile.getDashByIdOrThrow(dashId);

        ctx.writeAndFlush(produce(message.id, message.command, JsonParser.toJsonSharedDashboard(dashBoard)), ctx.voidPromise());
    }

}