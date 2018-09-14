//package server.security;
//
////import org.apache.cxf.interceptor.Fault;
////import org.apache.cxf.message.Message;
////import org.apache.cxf.phase.AbstractPhaseInterceptor;
////import org.apache.cxf.phase.Phase;
//import org.springframework.web.context.support.SpringBeanAutowiringSupport;
////import server.beans.ContextHolder;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
////import javax.ws.rs.core.Response;
//import java.util.List;
//import java.util.TreeMap;
//
///**
// * Created by pkrasnyuk on 08.06.2016.
// */
//public class CustomSessionHeaderWSInterceptor extends AbstractPhaseInterceptor<Message> {
//
//    @Inject
//    private MachineSessionTracker machineSessionTracker;
//
//    @Inject
//    ContextHolder contextHolder;
////    @Inject
////    Machine
//
//    @PostConstruct
//    public void init() {
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//    }
//
//    public CustomSessionHeaderWSInterceptor() {
//        super(Phase.PRE_INVOKE);
//    }
//
//    @Override
//    public void handleMessage(Message message) throws Fault {
//        try {
//            @SuppressWarnings("unchecked")
//            TreeMap<String, List<String>> headers = (TreeMap<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
//
//            String sessionId = headers.get("SESSIONID").get(0);
//
//            boolean result = machineSessionTracker.checkSessionId(sessionId);
//
//            if(!result) {
//                Response response = Response
//                        .status(Response.Status.UNAUTHORIZED)
//                        .entity("ACCESS DENIED")
//                        .build();
//                message.getExchange().put(Response.class, response);
//                return;
//            }
//
//            String machineCode = machineSessionTracker.getMachineBySession(sessionId);
//
//            contextHolder.setMachineCode(machineCode);
//            contextHolder.setSessionId(sessionId);
//            contextHolder.setDoctor(machineSessionTracker.getDoctorSession(sessionId));
//        }
//        catch (Exception ex) {
//            Response response = Response
//                    .status(Response.Status.UNAUTHORIZED)
//                    .entity("ACCESS DENIED")
//                    .build();
//            message.getExchange().put(Response.class, response);
//        }
//    }
//}
