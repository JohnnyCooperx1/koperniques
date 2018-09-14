//package server.security;
//
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class MachineSessionTracker {
//
//    private ConcurrentHashMap<String, String> activeSessions = new ConcurrentHashMap<>(500);
//    private ConcurrentHashMap<String, Doctor> activeDoctorSessions = new ConcurrentHashMap<>(100);
//
//    public String addSession(String sessionId, String machineId) {
//        if(activeSessions.containsValue(machineId)) {
//            for (Map.Entry<String, String> entry : activeSessions.entrySet()) {
//                if (entry.getValue().equals(machineId)) {
//                    return entry.getKey();
//                }
//            }
//        }
//
//        activeSessions.put(sessionId, machineId);
//        return sessionId;
//    }
//
//    public String addDoctorSession(String sessionId, Doctor doctor) {
//        activeDoctorSessions.put(sessionId, doctor);
//        return sessionId;
//    }
//
//    public String getSessionByMachineCode(String machineCode) {
//
//        for (Map.Entry<String, String> entry : activeSessions.entrySet()) {
//            if (entry.getValue() != null && entry.getValue().equals(machineCode)) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }
//
//    public Doctor getDoctorSession(String sessionId) {
//        return activeDoctorSessions.get(sessionId);
//    }
//
//    public void removeSession(String sessionId) {
//        activeSessions.remove(sessionId);
//    }
//
//    public String getMachineBySession(String sessionId) {
//        return activeSessions.get(sessionId);
//    }
//
//    public boolean checkSessionId(String sessionId) {
//        return activeSessions.containsKey(sessionId);
//    }
//
//    public void removeDoctorSession(String sessionId) {
//        activeDoctorSessions.remove(sessionId);
//    }
//}
