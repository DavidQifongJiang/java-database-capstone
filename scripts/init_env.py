"""Create local demo configuration without printing or committing secrets."""
from pathlib import Path
import base64
import secrets

target = Path(__file__).resolve().parents[1] / '.env'
if target.exists():
    raise SystemExit('.env already exists; it was not changed.')
target.write_text(
    f'CLINIC_ROOT_PASSWORD={secrets.token_urlsafe(32)}\n'
    f'CLINIC_DB_PASSWORD={secrets.token_urlsafe(32)}\n'
    f'CLINIC_JWT_SECRET={base64.b64encode(secrets.token_bytes(32)).decode()}\n'
    'CLINIC_DEMO_PASSWORD=DemoPassword1!\n', encoding='utf-8')
print('Created ignored .env with random database and signing secrets.')
