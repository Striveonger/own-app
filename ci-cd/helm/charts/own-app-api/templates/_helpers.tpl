{{/*
Expand the name of the chart.
*/}}
{{- define "own-app-api.name" -}}
{{- default .Chart.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "own-app-api.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "own-app-api.labels" -}}
helm.sh/chart: {{ include "own-app-api.chart" . }}
{{ include "own-app-api.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "own-app-api.selectorLabels" -}}
app.kubernetes.io/name: {{ include "own-app-api.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/namespace: {{ .Release.Namespace }}
{{- end }}
