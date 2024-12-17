import { useEffect } from "react";
import "./App.css";
import { Col, Row } from "react-bootstrap";
import Header from "./components/Header";
import Sidebar from "./components/Sidebar";
import { Navigate, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import Home from "./components/Home";
import EmployeeList from "./components/EmployeeManagement/ListEmployee/EmployeeList";
import CreateEmployee from "./components/EmployeeManagement/CreateEmployee/CreateEmployee";
import Login from "./components/Login";
import UpdateEmployee from "./components/EmployeeManagement/UpdateEmployee/UpdateEmployee";
import ForgotPassword from "./components/ForgotPassword";
import SetPassword from "./components/SetPassword";
import VaccineTypeList from "./components/VaccineTypeManagement/ListVaccineType/VaccineTypeList";
import CreateVaccineType from "./components/VaccineTypeManagement/CreateVaccineType/CreateVaccineType";
import UpdateVaccineType from "./components/VaccineTypeManagement/UpdateVaccineType/UpdateVaccineType";
import axios from "axios";
import VaccineList from "./components/VaccineManagement/ListVaccine/VaccineList";
import CreateVaccine from "./components/VaccineManagement/CreateVaccine/CreateVaccine";
import CreateSchedule from "./components/InjectionScheduleManagement/CreateSchedule/CreateSchedule";
import UpdateSchedule from "./components/InjectionScheduleManagement/UpdateSchedule/UpdateSchedule";
import UpdateVaccine from "./components/VaccineManagement/UpdateVaccine/UpdateVaccine";
import ImportVaccine from "./components/VaccineManagement/ImportVaccine/ImportVaccine";
import InjectionScheduleList from "./components/InjectionScheduleManagement/ListSchedule/InjectionScheduleList";
import CreateCustomer from "./components/CustomerManagement/CreateCustomer/CreateCustomer";
import CustomerList from "./components/CustomerManagement/ListCustomer/CustomerList";
import UpdateCustomer from "./components/CustomerManagement/UpdateCustomer/UpdateCustomer";
import CreateNews from "./components/NewsManagement/CreateNews";
import NewsList from "./components/NewsManagement/ListNews/NewsList";
import { showErrorAlert } from "./components/Utils/ErrorAlert";
import Error404 from "./components/ErrorHandling/Error404";
import UpdateNews from "./components/NewsManagement/UpdateNews";
import NewsDetail from "./components/NewsManagement/NewsDetail/NewsDetail";
import InjectionResultList from "./components/InjectionResultManagement/ListResult/InjectionResultList";
import ProtectedRoute from "./components/ProtectedRoute";
import Error403 from "./components/ErrorHandling/Error403";
import SelectSchedule from "./components/InjectionScheduleManagement/SelectSchedule/SelectSchedule";
import CreateResult from "./components/InjectionResultManagement/CreateResult/CreateResult";
import InjectionReportTable from "./components/InjectionReportManagement/InjectionReportTable";
import InjectionReportChart from "./components/InjectionReportManagement/InjectionReportChart";
import { ThemeProvider, createTheme } from '@mui/material/styles';
import RegisterInjection from "./components/RegisterInjection/RegisterInjection";
import BASE_URL from "./components/Api/BaseApi";
import VaccineReportTable from "./components/VaccineReportManagement/VaccineReportTable";
import VaccineReportChart from "./components/VaccineReportManagement/VaccineReportChart";
import CustomerReportTable from "./components/CustomerReportManagement/CustomerReportTable";
import CustomerReportChart from "./components/CustomerReportManagement/CustomerReportChart";

function App() {
    const location = useLocation();
    const noSidebarRoutes = ["/forgot-password", "/set-password", "/404", "/403"];
    const nav = useNavigate();
    const theme = createTheme();
    document.title = "Vaccination Management System";

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token !== null) {
            axios.post(`${BASE_URL}/vaccination/auth/introspect`, {
                "token": token
            })
                .then(response => {
                    if (response.data.valid !== true) {
                        localStorage.removeItem('userInfo');
                        localStorage.removeItem('token');
                        showErrorAlert(() => {
                            nav("/");
                        }, "Session expired! Please login again")
                    }
                })
                .catch(error => {
                    console.log(error);
                    localStorage.removeItem('userInfo');
                    localStorage.removeItem('token');
                    nav("/");
                })
        }
    }, [location, nav])

    return (
        <div className="App">
            <Row>
                <Header />
            </Row>
            <Row style={{ height: '88.5vh' }}>
                {!noSidebarRoutes.includes(location.pathname) && (
                    <Col lg={3} className="sidebar-row">
                        <Sidebar />
                    </Col>
                )}
                <Col lg={noSidebarRoutes.includes(location.pathname) ? 12 : 9}>
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/employee/list" element={<ProtectedRoute element={<EmployeeList />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/employee/create" element={<ProtectedRoute element={<CreateEmployee />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/employee/update/:id" element={<ProtectedRoute element={<UpdateEmployee />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/customer/list" element={<ProtectedRoute element={<CustomerList />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/customer/create" element={<ProtectedRoute element={<CreateCustomer />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/customer/update/:id" element={<ProtectedRoute element={<UpdateCustomer />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/vaccine_type/list" element={<ProtectedRoute element={<VaccineTypeList />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine_type/create" element={<ProtectedRoute element={<CreateVaccineType />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine_type/update/:id" element={<ProtectedRoute element={<UpdateVaccineType />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine/list" element={<ProtectedRoute element={<VaccineList />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine/create" element={<ProtectedRoute element={<CreateVaccine />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine/update/:id" element={<ProtectedRoute element={<UpdateVaccine />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/vaccine/import" element={<ProtectedRoute element={<ImportVaccine />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/schedule/list" element={<ProtectedRoute element={<InjectionScheduleList />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/schedule/create" element={<ProtectedRoute element={<CreateSchedule />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/schedule/update/:id" element={<ProtectedRoute element={<UpdateSchedule />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/result/list" element={<ProtectedRoute element={<InjectionResultList />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/result/create" element={<ProtectedRoute element={<SelectSchedule />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/result/create/:scheduleId" element={<ProtectedRoute element={<CreateResult />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/news/list" element={<ProtectedRoute element={<NewsList />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/news/create" element={<ProtectedRoute element={<CreateNews />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/news/details/:id" element={<ProtectedRoute element={<NewsDetail />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/news/update/:id" element={<ProtectedRoute element={<UpdateNews />} allowedRoles={['ROLE_ADMIN']} />} />
                        <Route path="/report/injection_result" element={<ProtectedRoute element={<InjectionReportTable />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/report/injection_result/chart" element={<ProtectedRoute
                            element={<ThemeProvider theme={theme}>
                                <InjectionReportChart />
                            </ThemeProvider>} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/report/customer" element={<ProtectedRoute element={<CustomerReportTable />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/report/customer/chart" element={<ProtectedRoute
                            element={<ThemeProvider theme={theme}>
                                <CustomerReportChart />
                            </ThemeProvider>} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/report/vaccine" element={<ProtectedRoute element={<VaccineReportTable />} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/report/vaccine/chart" element={<ProtectedRoute
                            element={<ThemeProvider theme={theme}>
                                <VaccineReportChart />
                            </ThemeProvider>} allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']} />} />
                        <Route path="/forgot-password" element={<ForgotPassword />} />
                        <Route path="/set-password" element={<SetPassword />} />
                        <Route path="/404" element={<Error404 />} />
                        <Route path="/403" element={<Error403 />}></Route>
                        <Route path="*" element={<Navigate replace to="/404" />} />
                        <Route path="/register-inject" element={<RegisterInjection />} />
                    </Routes>
                </Col>
            </Row>
        </div>
    );
}

export default App;
