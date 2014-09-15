package net.hueper.raspberry;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

@Path("/adxl345")
public class ADXL345Service
{

    @Autowired
    private ADXL345 adxl345;

    @GET
    @Path("/echo/{input}")
    @Produces("text/plain")
    public String ping(@PathParam("input") String input) {
        return input;
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/jsonBean")
    public Response modifyJson(JsonBean input) {
        input.setVal2(input.getVal1());
        return Response.ok().entity(input).build();
    }
    
    @GET
    @Path("/data")
    @Produces("application/json")
    public Response getADXL345Data() {
        try
        {
            ADXL345.AccelerationData accelerationData = adxl345.readAccelerationData();
            return Response.ok().entity(accelerationData).build();
        }
        catch (IOException e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}

