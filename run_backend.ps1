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
cd microservices/core-service-springboot
& "C:\Users\Dhipak\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd" spring-boot:run
