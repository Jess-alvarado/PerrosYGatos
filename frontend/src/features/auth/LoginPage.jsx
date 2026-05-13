import api from "../../api/api";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Mail, Lock, PawPrint } from "lucide-react";
import { jwtDecode } from "jwt-decode";

const LoginPage = ({ onFinishAuth }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post("/api/auth/login", {
        username: username,
        password: password,
      });

      const { accessToken, refreshToken } = response.data;

      if (accessToken) {
        localStorage.setItem("token", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        try {
          const decoded = jwtDecode(accessToken);
          console.log("Token decodificado:", decoded);

          const userObj = {
            firstname: decoded.firstname,
            lastname: decoded.lastname,
            email: decoded.sub,
            id: decoded.uid,
          };

          localStorage.setItem("user", JSON.stringify(userObj));

          const userRole = decoded.role || "ROLE_OWNER";

          if (onFinishAuth) {
            onFinishAuth(userRole);
          }

          console.log("Login exitoso, redirigiendo a perfil...");
          navigate("/profile");
        } catch (decodeError) {
          console.error("Error al decodificar el token:", decodeError);
          if (onFinishAuth) onFinishAuth("ROLE_OWNER");
          navigate("/profile");
        }
      }
    } catch (error) {
      console.error("Error en login:", error);
      const errorMsg =
        error.response?.data?.message ||
        "Credenciales inválidas o error de servidor.";
      alert(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-main)] p-4">
      <div className="w-full max-w-md bg-[var(--bg-surface)] rounded-card shadow-[var(--card-shadow)] p-10 border border-[var(--border-color)]">
        <div className="flex flex-col items-center mb-8">
          <div className="bg-[var(--color-terracotta)] p-3 rounded-2xl mb-4 shadow-lg">
            <PawPrint className="text-white w-7 h-7" />
          </div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">
            Bienvenido de nuevo
          </h1>
          <p className="text-[var(--text-muted)] text-sm mt-1">
            Ingresa tus datos para continuar
          </p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold mb-2 ml-1">
              Usuario / Email
            </label>
            <div className="relative">
              <Mail className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
              <input
                type="email"
                required
                placeholder="test-1@gg.com"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold mb-2 ml-1">
              Contraseña
            </label>
            <div className="relative">
              <Lock className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
              <input
                type="password"
                required
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-[var(--color-terracotta)] hover:bg-[#c4664a] text-white font-bold py-4 rounded-2xl shadow-lg transition-all disabled:opacity-50"
          >
            {loading ? "Iniciando sesión..." : "Entrar"}
          </button>
        </form>

        <p className="text-center text-[var(--text-muted)] text-sm mt-8">
          ¿No tienes cuenta?{" "}
          <button
            onClick={() => navigate("/register")}
            className="text-[var(--color-terracotta)] font-bold hover:underline bg-transparent border-none cursor-pointer"
          >
            Regístrate aquí
          </button>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
