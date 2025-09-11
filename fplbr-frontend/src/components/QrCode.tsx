
type Props = { otpauthUrl: string }

export default function QrCode({ otpauthUrl }: Props) {
  // Usa api de chart para gerar QR sem dependência
  const src = `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(otpauthUrl)}`
  return <img src={src} alt="QR Code TOTP" width={180} height={180} />
}


