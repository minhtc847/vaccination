import {Bounce, Flip, Slide, Theme, toast, ToastPosition, Zoom} from 'react-toastify';

type ToastTransition = typeof Bounce | typeof Slide | typeof Zoom | typeof Flip;

interface ToastProps {
    position?: ToastPosition;
    type?: 'info' | 'success' | 'warning' | 'error';
    message: string;
    theme?: Theme;
    transition?: ToastTransition;
}

export const Toast_Custom = (
    {
        position = 'top-right',
        type = 'success',
        message,
        theme = 'light',
        transition = Bounce,
    }: ToastProps): void => {
    toast[type](message, {
        position,
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme,
        transition,
    });
};