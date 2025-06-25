{{/* vim: set filetype=mustache: */}}
{{/* 这里的模版中, 绑死了values中的端口号, 当有多个服务时, .app 需要抽象出来, 这里就不弄辣么麻烦了 */}}
{{/* livenessProbe template */}}
{{- define "common.probe.liveness" }}
httpGet:
  path: /actuator/health/liveness
  port: {{ .Values.app.port }}
initialDelaySeconds: 5
failureThreshold: 3
periodSeconds: 15
timeoutSeconds: 10
{{- end -}}

{{/* readinessProbe: template */}}
{{- define "common.probe.readiness" }}
httpGet:
  path: /actuator/health/readiness
  port: {{ .Values.app.port }}
initialDelaySeconds: 45
failureThreshold: 3
periodSeconds:  60
timeoutSeconds:  10
{{- end -}}