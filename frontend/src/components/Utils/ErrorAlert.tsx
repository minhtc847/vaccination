import Swal from 'sweetalert2';

export const showErrorAlert = (onClose: () => void, message: string) => {
    Swal.fire({
        icon: 'error',
        title: 'Error',
        text: message,
        timer: 4000,
        timerProgressBar: true,
        customClass: {
            confirmButton: 'swal2-confirm'
        },
        didClose: onClose
    });
};