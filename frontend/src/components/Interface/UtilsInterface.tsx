import { Vaccine, InjectionSchedule, Customer, Employee } from "./BusinessObjectInterface";

export interface Search<Object> {
    content: Object[];
    pageNo: number;
    pageSize: number;
    totalElements: number;
    totalPaged: number;
    last: boolean;
}

export interface ProtectedRouteProps {
    element: JSX.Element;
    allowedRoles: string[];
}

export interface AlertModalProps {
    show: boolean;
    handleClose: () => void;
    message: string;
}

export interface ConfirmModalProps {
    show: boolean;
    handleClose: () => void;
    message: string;
    handleLogic: () => void;
}

export interface EmployeeDetailModalProps {
    show: boolean;
    employee: Employee | null;
    handleClose: () => void;
}

export interface InjectionScheduleDetailModalProps {
    show: boolean;
    schedule: InjectionSchedule | null;
    handleClose: () => void;
}

export interface CustomerDetailModalProps {
    show: boolean;
    customer: Customer | null;
    handleClose: () => void;
}

export interface PagingProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (pageNumber: number) => void;
}

export interface SidebarItemProps {
    item: {
        title: string;
        path?: string;
        childrens?: SidebarItemProps['item'][];
    };
    currentPath: string;
}

export interface ChartData {
    month: string,
    value: number,
}