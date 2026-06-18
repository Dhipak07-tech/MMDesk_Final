# Start Spring Boot Server in Test Profile
$Root = $PSScriptRoot
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
$env:Path = "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot\bin;" + $Root + "\maven\apache-maven-3.9.6\bin;" + $env:Path

# Load environment variables from .env file
$EnvFile = Join-Path $Root ".env"
if (Test-Path $EnvFile) {
    Write-Host "Loading environment variables from .env..."
    Get-Content $EnvFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
            $key, $val = $line -split '=', 2
            $key = $key.Trim()
            $val = $val.Trim()
            if (($val.StartsWith('"') -and $val.EndsWith('"')) -or ($val.StartsWith("'") -and $val.EndsWith("'"))) {
                $val = $val.Substring(1, $val.Length - 2)
            }
            [System.Environment]::SetEnvironmentVariable($key, $val, [System.EnvironmentVariableTarget]::Process)
        }
    }
}

Write-Host "Starting Spring Boot Server in Test Profile (H2/M365)..."
$TargetDir = Join-Path $Root "microservices\core-service-springboot"
cd $TargetDir
java "-Dspring.profiles.active=test" -jar target/ticklora-core-1.0.0.jar
