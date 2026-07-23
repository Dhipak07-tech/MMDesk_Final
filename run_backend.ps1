# run_backend.ps1
# Load environment variables from .env file
if (Test-Path .env) {
    Write-Host "Loading environment variables from .env..."
    Get-Content .env | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            if ($line -match '^([^=]+)=(.*)$') {
                $key = $Matches[1].Trim()
                $value = $Matches[2].Trim()
                # Remove surrounding quotes if present
                if ($value.StartsWith('"') -and $value.EndsWith('"')) {
                    $value = $value.Substring(1, $value.Length - 2)
                } elseif ($value.StartsWith("'") -and $value.EndsWith("'")) {
                    $value = $value.Substring(1, $value.Length - 2)
                }
                [System.Environment]::SetEnvironmentVariable($key, $value, [System.EnvironmentVariableTarget]::Process)
                Write-Host "Set $key"
            }
        }
    }
} else {
    Write-Warning ".env file not found!"
}

# Run Maven Spring Boot target
Write-Host "Starting Spring Boot backend..."
$env:JAVA_HOME = "C:\Users\aakas\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
cd microservices/core-service-springboot
& "C:\Users\aakas\.m2\wrapper\dists\apache-maven-3.9.6\0f95e7798d182e3371f3fee6d8202d3a56e6d71dfd3a2402139882ef2fbe7476\bin\mvn.cmd" spring-boot:run
