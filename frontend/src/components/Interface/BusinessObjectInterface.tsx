export interface UserInfo {
    username: string;
    email: string;
    role: string;
    image: string;
}

export interface InjectionResult {
    id: number,
    customer: Customer,
    injectionDate: string,
    result: string,
    vaccine: Vaccine,
    injectionSchedule: InjectionSchedule,
    nextInjectionDate: string,
    injectionTime: number,

}

export interface VaccineType {
    id: number;
    code: string;
    description: string;
    vaccineTypeName: string;
    image: string;
    status: boolean;
}

export interface Vaccine {
    id: number;
    vaccineTypeId: number;
    vaccineName: string;
    usage: string;
    indication: string;
    contraindication: string;
    numberOfInjection: number;
    timeBeginNextInjection: string;
    timeEndNextInjection: string;
    origin: string;
    status: boolean;
    vaccineTypeName: string;
    totalInject: number;
}

export interface InjectionSchedule {
    id: number;
    startDate: string;
    endDate: string;
    description: string;
    place: string;
    status: string;
    vaccineId: number;
    injectionTimes: number;
    vaccineName: string;
}

export interface News {
    id: number;
    content: string;
    preview: string;
    title: string;
    date: string;
}

export interface Customer {
    id: number;
    address: string;
    dateOfBirth: string;
    email: string;
    employeeName: string;
    gender: string;
    image: string;
    phone: string;
    username: string;
    identityCard: string;
    version: number;
}

export interface Employee {
    employeeId: number;
    address: string;
    dateOfBirth: string;
    email: string;
    employeeName: string;
    gender: string;
    image: null;
    phone: string;
    position: string;
    workingPlace: string;
}

export interface InjectionResultData {
    customer: Customer[],
    vaccineName: string,
    vaccineType: string,
    injection: number,
    injectionDate: string,
    injectionNextDate: string,
    schedule: string,
}

export interface InjectionResultReport {
    vaccineName: string,
    usage: string,
    customerName: string,
    dateOfInjection: string,
}

export interface ReportChart {
    [month: string]: number,
}

export interface VaccineReport {
    vaccineName: string,
    vaccineType: string,
    numberOfInjection: number,
    totalInjection: number,
    timeBeginNextInjection: number,
    origin: string,
}

export interface CustomerReport {
    employeeName: string,
    dateOfBirth: string,
    address: string,
    identityCard: string,
    numberOfInject: number,
}