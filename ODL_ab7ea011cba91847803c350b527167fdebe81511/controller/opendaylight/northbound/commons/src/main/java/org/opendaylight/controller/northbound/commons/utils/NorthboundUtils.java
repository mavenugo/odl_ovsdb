package org.opendaylight.controller.northbound.commons.utils;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.opendaylight.controller.containermanager.IContainerAuthorization;
import org.opendaylight.controller.sal.authorization.Privilege;
import org.opendaylight.controller.sal.authorization.UserLevel;
import org.opendaylight.controller.sal.utils.GlobalConstants;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.controller.usermanager.IUserManager;

public class NorthboundUtils {

    private static final Map<StatusCode, Response.Status> ResponseStatusMapping = new HashMap<StatusCode, Response.Status>() {
        private static final long serialVersionUID = 1L;
        {
            put(StatusCode.SUCCESS, Response.Status.OK);
            put(StatusCode.BADREQUEST, Response.Status.BAD_REQUEST);
            put(StatusCode.UNAUTHORIZED, Response.Status.UNAUTHORIZED);
            put(StatusCode.FORBIDDEN, Response.Status.FORBIDDEN);
            put(StatusCode.NOTFOUND, Response.Status.NOT_FOUND);
            put(StatusCode.NOTALLOWED, Response.Status.FORBIDDEN);
            put(StatusCode.NOTACCEPTABLE, Response.Status.NOT_ACCEPTABLE);
            put(StatusCode.TIMEOUT, Response.Status.GONE);
            put(StatusCode.CONFLICT, Response.Status.CONFLICT);
            put(StatusCode.GONE, Response.Status.GONE);
            put(StatusCode.UNSUPPORTED, Response.Status.BAD_REQUEST);
            put(StatusCode.INTERNALERROR, Response.Status.INTERNAL_SERVER_ERROR);
            put(StatusCode.NOTIMPLEMENTED, Response.Status.NOT_ACCEPTABLE);
            put(StatusCode.NOSERVICE, Response.Status.SERVICE_UNAVAILABLE);
            put(StatusCode.UNDEFINED, Response.Status.BAD_REQUEST);
        }
    };

    // Suppress default constructor for noninstantiability
    private NorthboundUtils() {
    }

    /**
     * Returns Response.Status for a given status. If the status is null or if
     * the corresponding StatusCode is not present in the ResponseStatusMapping,
     * it returns null.
     *
     * @param status
     *            The Status
     * @return The Response.Status for a given status
     */
    public static Response.Status getResponseStatus(Status status) {
        return ResponseStatusMapping.get(status.getCode());
    }

    /**
     * Returns Response for a given status. If the status provided is null or if
     * the corresponding StatusCode is not present in the ResponseStatusMapping,
     * it returns Response with StatusType as INTERNAL_SERVER_ERROR.
     *
     * @param status
     *            The Status
     * @return The Response for a given status.
     */
    public static Response getResponse(Status status) {
        if ((status == null) || (!ResponseStatusMapping.containsKey(status.getCode()))) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Action Result Unknown").build();
        }
        return Response.status(ResponseStatusMapping.get(status.getCode())).entity(status.getDescription()).build();
    }

    /**
     * Returns whether the current user has the required privilege on the
     * specified container
     *
     * @param userName
     *            The user name
     * @param containerName
     *            The container name
     * @param required
     *            Operation to be performed - READ/WRITE
     * @param bundle
     *            Class from where the function is invoked
     * @return The Status of the request, either Success or Unauthorized
     */
    public static boolean isAuthorized(String userName, String containerName, Privilege required, Object bundle) {

        if (containerName.equals(GlobalConstants.DEFAULT.toString())) {
            IUserManager auth = (IUserManager) ServiceHelper.getGlobalInstance(IUserManager.class, bundle);

            switch (required) {
            case WRITE:
                return (auth.getUserLevel(userName).ordinal() <= UserLevel.NETWORKADMIN.ordinal());
            case READ:
                return (auth.getUserLevel(userName).ordinal() <= UserLevel.NETWORKOPERATOR.ordinal());
            default:
                return false;
            }

        } else {
            IContainerAuthorization auth = (IContainerAuthorization) ServiceHelper.getGlobalInstance(
                    IContainerAuthorization.class, bundle);

            if (auth == null) {
                return false;
            }

            Privilege current = auth.getResourcePrivilege(userName, containerName);
            if (required.ordinal() > current.ordinal()) {
                return false;
            }
        }
        return true;
    }

}
