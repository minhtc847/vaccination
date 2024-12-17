import { z } from 'zod';

export const CreateCustomerSchema = z.object({
    employeeName: z.string().min(1, "Employee Name is required!").max(100, "Employee Name must be less than 100 characters").regex(/^[a-zA-Z\sàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđĐ]+$/, "Employee must be contain only characters"),
    dateOfBirth: z.string().date("Invalid Date of Birth"),
    gender: z.string(),
    identityCard: z.string().length(12, "Invalid Identity Card"),
    username: z.string().min(8, "Username must be at least 8 characters long"),
    password: z.string()
        .regex(new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/), "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
        .min(8, "Password must be at least 8 characters long"),
    passwordConfirm: z.string(),
    email: z.string().email("Invalid Email Address").min(1, "Email is required!").max(100, "Email must be less than 100 characters"),
    phone: z.string().regex(new RegExp(/^0[0-9]{9,13}$/), "Invalid phone number, phone number must start with 0 and have 10-13 digits"),
    captcha: z.string(),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.dateOfBirth) <= new Date(),
    {
        message: "Date of Birth must be before today",
        path: ["dateOfBirth"],
    }
).refine(
    (data) => {
        const birthDate = new Date(data.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return (age >= 18 && age <= 100);
    },
    {
        message: "Invalid age, age must be greater than 18 and less than 100",
        path: ["dateOfBirth"],
    }
).refine(
    (data) => data.password === data.passwordConfirm,
    {
        message: "Passwords does not match",
        path: ["passwordConfirm"],
    }
);

export const UpdateCustomerSchema = z.object({
    employeeName: z.string().min(1, "Employee Name is required!").max(100, "Employee Name must be less than 100 characters").regex(/^[a-zA-Z\sàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđĐ]+$/, "Employee must be contain only characters"),
    dateOfBirth: z.string().date().min(1, "Date of Birth is required!"),
    gender: z.string(),
    identityCard: z.string().length(12, "Invalid Identity Card"),
    email: z.string().email("Invalid Email Address").min(1, "Email is required!").max(100, "Email must be less than 100 characters"),
    phone: z.string().regex(new RegExp(/^0[0-9]{9,13}$/), "Invalid phone number, phone number must start with 0 and have 10-13 digits"),
    captcha: z.string(),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.dateOfBirth) <= new Date(),
    {
        message: "Date of Birth must be before today",
        path: ["dateOfBirth"],
    }
).refine(
    (data) => {
        const birthDate = new Date(data.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return (age >= 18 && age <= 100);
    },
    {
        message: "Invalid age, age must be greater than 18 and less than 100",
        path: ["dateOfBirth"],
    }
);

export const CreateEmployeeSchema = z.object({
    username: z.string().min(8, "Username must be at least 8 characters long").max(255, "Username must be less than 255 characters"),
    email: z.string().email("Invalid Email Address").min(1, "Email is required!").max(100, "Email must be less than 100 characters"),
    employeeName: z.string().min(1, "Employee Name is required!").max(100, "Employee Name must be less than 100 characters").regex(/^[a-zA-Z\sàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđĐ]+$/, "Employee must be contain only characters"),
    gender: z.string(),
    dateOfBirth: z.string().date("Invalid Date of Birth"),
    phone: z.string().regex(new RegExp(/^0[0-9]{9,13}$/), "Invalid phone number, phone number must start with 0 and have 10-13 digits"),
    position: z.string().min(1, "Position is required!"),
    workingPlace: z.string().max(100, "Working Place must be less than 100 characters"),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.dateOfBirth) <= new Date(),
    {
        message: "Date of Birth must be before today",
        path: ["dateOfBirth"],
    }
).refine(
    (data) => {
        const birthDate = new Date(data.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return (age >= 18 && age <= 65);
    },
    {
        message: "Invalid age, age must be greater than 18 and less than 65",
        path: ["dateOfBirth"],
    }
);

export const UpdateEmployeeSchema = z.object({
    email: z.string().email("Invalid Email Address").min(1, "Email is required!").max(255, "Email must be less than 255 characters"),
    employeeName: z.string().min(1, "Employee Name is required!").max(255, "Employee Name must be less than 255 characters").regex(/^[a-zA-Z\sàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđĐ]+$/, "Employee must be contain only characters"),
    gender: z.string(),
    dateOfBirth: z.string().date().min(1, "Date of Birth is required!"),
    phone: z.string().regex(new RegExp(/^0[0-9]{9,13}$/), "Invalid phone number, phone number must start with 0 and have 10-13 digits"),
    position: z.string().min(1, "Position is required!"),
    workingPlace: z.string().max(255, "Working Place must be less than 255 characters"),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.dateOfBirth) <= new Date(),
    {
        message: "Date of Birth must be before today",
        path: ["dateOfBirth"],
    }
).refine(
    (data) => {
        const birthDate = new Date(data.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return (age >= 18 && age <= 65);
    },
    {
        message: "Invalid age, age must be greater than 18 and less than 65",
        path: ["dateOfBirth"],
    }
);

export const CreateScheduleSchema = z.object({
    vaccineId: z.number().min(1, "Vaccine is required!"),
    startDate: z.string().date().min(1, "Start time is required!"),
    endDate: z.string().date().min(1, "End time is required!"),
    description: z.string().max(200, "Description must be less than 200 characters").nullable(),
    injectionTimes: z.number({
        required_error: "Injection times is required!",
        invalid_type_error: "Injection times must be a number"
    }).gt(0, "Injection times must be greater than 0"),
    injectPerDay: z.number({
        required_error: "Inject Per Day is required!",
        invalid_type_error: "Inject per day must be a number"
    }).gt(0, "Inject per day must be greater than 0"),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.endDate) >= new Date(data.startDate),
    {
        message: `“End Date” must not be earlier than “Start Date”.`,
        path: ["endDate"],
    }
);

export const UpdateScheduleSchema = z.object({
    vaccineId: z.number().min(1, "Vaccine is required!"),
    startDate: z.string().date().min(1, "Start time is required!"),
    endDate: z.string().date().min(1, "End time is required!"),
    description: z.string().max(200, "Description must be less than 200 charactesr").nullable(),
    injectionTimes: z.number({
        required_error: "Injection times is required!",
        invalid_type_error: "Injection times must be a number"
    }).gt(0, "Injection times must be greater than 0"),
    injectPerDay: z.number({
        required_error: "Inject Per Day is required!",
        invalid_type_error: "Inject per day must be a number"
    }).gt(0, "Inject per day must be greater than 0"),
    city: z.string().min(1, "City is required!"),
    district: z.string().min(1, "District is required!"),
    ward: z.string().min(1, "Ward is required!"),
    streetAddress: z.string().min(1, "Street Address is required!").max(100, "Street Address must be less than 100 characters"),
}).passthrough().refine(
    (data) => new Date(data.endDate) >= new Date(data.startDate),
    {
        message: `“End Date” must not be earlier than “Start Date”.`,
        path: ["endDate"],
    }
);

export const CreateVaccineSchema = z.object({
    vaccineName: z.string().min(1, "Vaccine Name is required!").max(50, "Vaccine Name must be less than 50 characters"),
    usage: z.string().max(200, "Usage must be less than 200 characters").min(1, "Usage is required!"),
    vaccineTypeId: z.number({
        invalid_type_error: "Vaccine Type is required!"
    }).min(1, "Vaccine Type is required!"),
    indication: z.string().max(200, "Indication must be less than 200 characters").min(1, "Indication is required!"),
    contraindication: z.array(z.string().max(200, "Contraindication must be less than 200 characters")),
    numberOfInjection: z.number({
        invalid_type_error: "Number of injection is required!"
    }).gt(0, "Number of injection must be greater than 0").min(1, "Number of injection is required!"),
    totalInject: z.number({
        invalid_type_error: "Total inject is required!"
    }).gt(0, "Total inject must be greater than 0").min(1, "Total inject is required!"),
    timeBeginNextInjection: z.number({
        invalid_type_error: "Time begin next injection is required!"
    }).gt(0, "Time begin next injection must be greater than 0").min(1, "Time begin next injection is required!"),
    origin: z.string().min(1, "Origin is required!").max(50, "Origin must be less than 50 characters"),
}).passthrough();

export const UpdateVaccineSchema = z.object({
    vaccineName: z.string().min(1, "Vaccine Name is required!").max(50, "Vaccine Name must be less than 50 characters"),
    usage: z.string().max(200, "Usage must be less than 200 characters").min(1, "Usage is required!"),
    vaccineTypeId: z.number({}).min(1, "Vaccine Type is required!"),
    indication: z.string().max(200, "Indication must be less than 200 characters").min(1, "Indication is required!"),
    contraindication: z.array(z.string().max(200, "Contraindication must be less than 200 characters")),
    numberOfInjection: z.number({}).gt(0, "Number of injection must be greater than 0").min(1, "Number of injection is required!"),
    totalInject: z.number({
        invalid_type_error: "Total inject must be a number"
    }).gt(0, "Total inject must be greater than 0").min(1, "Total inject is required!"),
    timeBeginNextInjection: z.number({
        invalid_type_error: "Time begin next injection must be a number"
    }).gt(0, "Time begin next injection must be greater than 0").min(1, "Time begin next injection is required!"),
    origin: z.string().min(1, "Origin is required!").max(50, "Origin must be less than 50 characters"),
    status: z.boolean().optional(),
}).passthrough();

export const CreateVaccineTypeSchema = z.object({
    description: z.string().min(1, "Description is required!").max(200, "Description must be less than 200 characters"),
    vaccineTypeName: z.string().min(1, "Vaccine Type Name is required!").max(50, "Vaccine Type Name must be less than 50 characters"),
}).passthrough()

export const UpdateVaccineTypeSchema = z.object({
    code: z.string().optional(),
    description: z.string().min(1, "Description is required!").max(200, "Description must be less than 200 characters"),
    vaccineTypeName: z.string().min(1, "Vaccine Type Name is required!").max(50, "Vaccine Type Name must be less than 50 characters"),
    status: z.boolean().optional(),
    image: z.string().optional(),
}).passthrough();

export const CreateResultSchema = z.object({
    customerName: z.string().min(1, "Customer is required!"),
}).passthrough();