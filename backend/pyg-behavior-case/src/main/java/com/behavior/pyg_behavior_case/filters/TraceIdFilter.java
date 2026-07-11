package com.behavior.pyg_behavior_case.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1) // corre antes que el JwtSignatureFilter, para que hasta los 401 queden logueados con traceid
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);

        if (traceId == null || traceId.isBlank()) {
            traceId = "no-trace-id"; // no debería pasar si viene del gateway, pero por si acaso
        }

        try {
            MDC.put(TRACE_ID_MDC_KEY, traceId);
            chain.doFilter(request, response);
        } finally {
            // Limpieza obligatoria — si no se limpia, el traceId de
            // una request puede "filtrarse" a logs de la siguiente
            // request si el thread se reutiliza (thread pool).
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}