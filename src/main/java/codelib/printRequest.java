//package main.java.codelib;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class printRequest {
//
//    private void logRequest(HttpServletRequest request) {
//        LogFormatUtils.traceDebug(logger, traceOn -> {
//            String params;
//            if (isEnableLoggingRequestDetails()) {
//                params = request.getParameterMap().entrySet().stream()
//                        .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
//                        .collect(Collectors.joining(", "));
//            }
//            else {
//                params = (request.getParameterMap().isEmpty() ? "" :  "masked");
//            }
//
//            String query = StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString();
//            String dispatchType = (!request.getDispatcherType().equals(DispatcherType.REQUEST) ?
//                    "\"" + request.getDispatcherType().name() + "\" dispatch for " : "");
//            String message = (dispatchType + request.getMethod() + " \"" + getRequestUri(request) +
//                    query + "\", parameters={" + params + "}");
//
//            if (traceOn) {
//                List<String> values = Collections.list(request.getHeaderNames());
//                String headers = values.size() > 0 ? "masked" : "";
//                if (isEnableLoggingRequestDetails()) {
//                    headers = values.stream().map(name -> name + ":" + Collections.list(request.getHeaders(name)))
//                            .collect(Collectors.joining(", "));
//                }
//                return message + ", headers={" + headers + "} in DispatcherServlet '" + getServletName() + "'";
//            }
//            else {
//                return message;
//            }
//        });
//    }
//}
