package io.penguinstats.api;

import com.sun.jersey.spi.resource.Singleton;
import io.penguinstats.bean.ItemDrop;
import io.penguinstats.bean.ScreenShot;
import io.penguinstats.service.ScreenShotsService;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/screenshots")
@Singleton
public class ScreenShotsAPI {

    private static final ScreenShotsService screenShotsService = ScreenShotsService.getInstance();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllItems(ScreenShot screenShot) {
        ItemDrop itemDrop = screenShotsService.matchDrops(screenShot);
        return Response.ok().entity(itemDrop).build();
    }

}
