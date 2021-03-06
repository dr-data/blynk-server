package cc.blynk.server.core.model.widgets.others.rtc;

import cc.blynk.server.core.model.Pin;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.controls.HardwareSyncWidget;
import cc.blynk.utils.DateTimeUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.netty.channel.ChannelHandlerContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.utils.BlynkByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.03.15.
 */
public class RTC extends OnePinWidget implements HardwareSyncWidget {

    @JsonSerialize(using = ZoneIdToString.class)
    @JsonDeserialize(using = StringToZoneId.class, as = ZoneId.class)
    public ZoneId tzName;

    @Override
    public String getModeType() {
        return null;
    }

    @Override
    public String makeHardwareBody() {
        return null;
    }

    @Override
    public int getPrice() {
        return 100;
    }

    @Override
    public boolean isRequiredForSyncAll() {
        return false;
    }

    @Override
    public void send(ChannelHandlerContext ctx, int msgId) {
        final String body = Pin.makeHardwareBody(pinType, pin, getTime());
        ctx.write(makeUTF8StringMessage(HARDWARE, msgId, body), ctx.voidPromise());
    }

    private String getTime() {
        ZoneId zone;
        if (tzName != null) {
            zone = tzName;
        } else {
            zone = DateTimeUtils.UTC;
        }

        LocalDateTime ldt = LocalDateTime.now(zone);
        return String.valueOf(ldt.toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public String getJsonValue() {
        return "[" + getTime() + "]";
    }
}
