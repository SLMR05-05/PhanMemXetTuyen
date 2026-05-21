import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../context/authStore';
import loginBackground from '../img/LoginPage.jpg';
import sguLogo from '../img/SGU-Logo.jpg';

export default function LoginPage() {
    const [cccd, setCccd] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();
    const { login, error, clearError } = useAuthStore();

    const handleSubmit = async (e) => {
        e.preventDefault();
        clearError();
        setIsLoading(true);

        try {
            await login(cccd, password);
            navigate('/dashboard');
        } catch (err) {
            console.error('Login error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-h-screen bg-white lg:grid lg:grid-cols-[3fr_1fr]">
            <div className="relative hidden lg:block overflow-hidden">
                <img
                    src={loginBackground}
                    alt="Truong Dai hoc Sai Gon"
                    className="h-full w-full object-cover login-image-pan"
                />
                <div className="absolute inset-0 bg-gradient-to-r from-black/10 via-transparent to-black/20" />
            </div>

            <div className="flex flex-col  min-h-screen items-center justify-center p-4 sm:p-8 lg:p-6 login-panel-fade">
                <div className="mb-4 flex justify-center">
                    <img
                        src={sguLogo}
                        alt="Logo SGU"
                        className="h-9/12 w-9/12 object-contain"
                    />
                </div>

                <p className="mb-4 text-center text-[18px] font-semibold text-[#0b5b88] sm:text-[20px]">
                    Hệ Thống Xét Tuyển - Tuyển Sinh SGU
                </p>
                
                <div className="w-full max-w-sm rounded-md border border-slate-300 bg-slate-50 p-4 shadow-sm login-card-rise">
                    {error && (
                        <div className="mb-4 rounded border border-red-300 bg-red-50 px-3 py-2 text-sm text-red-700">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-3">
                        <div className="flex h-11 overflow-hidden rounded border border-slate-300 bg-white focus-within:ring-2 focus-within:ring-cyan-400">
                            <div className="grid w-11 place-items-center border-r border-slate-300 bg-slate-100 text-slate-600">
                                <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor" aria-hidden="true">
                                    <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5Zm0 2c-4.42 0-8 2.24-8 5v1h16v-1c0-2.76-3.58-5-8-5Z" />
                                </svg>
                            </div>
                            <input
                                type="text"
                                value={cccd}
                                onChange={(e) => setCccd(e.target.value)}
                                placeholder="CCCD"
                                className="w-full px-3 text-[17px] text-slate-700 outline-none"
                                required
                            />
                        </div>

                        <div className="flex h-11 overflow-hidden rounded border border-slate-300 bg-white focus-within:ring-2 focus-within:ring-cyan-400">
                            <div className="grid w-11 place-items-center border-r border-slate-300 bg-slate-100 text-slate-600">
                                <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor" aria-hidden="true">
                                    <path d="M17 8h-1V6a4 4 0 0 0-8 0v2H7a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2Zm-7-2a2 2 0 1 1 4 0v2h-4Z" />
                                </svg>
                            </div>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Mật khẩu"
                                className="w-full px-3 text-[17px] text-slate-700 outline-none"
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword((prev) => !prev)}
                                className="grid w-12 place-items-center border-l border-slate-300 bg-slate-100 text-slate-600 transition hover:bg-slate-200"
                                aria-label={showPassword ? 'An mat khau' : 'Hien mat khau'}
                            >
                                {showPassword ? (
                                    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor" aria-hidden="true">
                                        <path d="m3.28 2 18.72 18.72-1.27 1.27-3.2-3.2A12.9 12.9 0 0 1 12 20c-4.79 0-8.93-2.94-11-7a12.93 12.93 0 0 1 4.36-4.94L2 3.27ZM9.53 8.26A4 4 0 0 0 15.74 14.47Zm3.01-2.95a4 4 0 0 1 5.79 3.57 3.92 3.92 0 0 1-.6 2.09L13.6 6.84a3.93 3.93 0 0 1-1.06.47Zm8.37 7.7a13.35 13.35 0 0 0-3.43-4.23l-1.44-1.44A11.54 11.54 0 0 0 12 6c-.64 0-1.26.05-1.87.16L8.51 4.54A13.67 13.67 0 0 1 12 4c4.79 0 8.93 2.94 11 7-.33.66-.7 1.28-1.09 1.88Z" />
                                    </svg>
                                ) : (
                                    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor" aria-hidden="true">
                                        <path d="M12 5c4.79 0 8.93 2.94 11 7-2.07 4.06-6.21 7-11 7S3.07 16.06 1 12c2.07-4.06 6.21-7 11-7Zm0 2a5 5 0 1 0 5 5 5 5 0 0 0-5-5Zm0 2a3 3 0 1 1-3 3 3 3 0 0 1 3-3Z" />
                                    </svg>
                                )}
                            </button>
                        </div>

                        {/* <div className="pt-1 text-right">
                            <button type="button" className="border-0 bg-transparent p-0 text-[14px] text-[#0077b8] transition hover:text-[#005a8c] focus:outline-none">
                                Quên Mật Khẩu
                            </button>
                        </div> */}

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="login-btn-shine mt-2 flex h-11 w-full items-center justify-center gap-2 rounded bg-[#005f92] px-4 font-semibold text-white transition hover:bg-[#004e79] disabled:cursor-not-allowed disabled:bg-slate-400"
                        >
                            <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor" aria-hidden="true">
                                <path d="M10 17.5 15.5 12 10 6.5v3.75H3v3.5h7Zm4-11h4a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2h-4V20h4a4 4 0 0 0 4-4V8a4 4 0 0 0-4-4h-4Z" />
                            </svg>
                            {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
