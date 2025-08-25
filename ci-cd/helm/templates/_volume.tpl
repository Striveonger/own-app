{{/* 容器挂载配置 */}}
{{- define "common.volumeMounts" -}}
- name: app-logs
  mountPath: {{.Values.global.log.path | default "/var/log/"}}
  readOnly: false
- name: app-config
  mountPath: /opt/app/configs
  readOnly: true
{{- end -}}

{{/* 应用日志卷 */}}
{{- define "common.log.volumes" -}}
- name: app-logs
  hostPath:
    path: /var/logs/own/app
    type: "DirectoryOrCreate"
{{- end -}}

{{/* 应用配置卷 */}}
{{- define "common.app.config.volumes" -}}
- name: app-config
  configMap:
    name: {{ .Release.Name }}-app-config-map
{{- end -}}
