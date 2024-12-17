import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import AlertModal from '../../Modals/AlertModal';
import ConfirmModal from '../../Modals/ConfirmModal';
import { Employee } from '../../Interface/BusinessObjectInterface';
import EmployeeDetailModal from './EmployeeDetailModal';
import { useDebounce } from "@uidotdev/usehooks";
import Paging from '../../Utils/Paging';
import { showErrorAlert } from '../../Utils/ErrorAlert';
import { Bounce, toast } from 'react-toastify';
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';
import { Search } from '../../Interface/UtilsInterface';
import BASE_URL from '../../Api/BaseApi';

const EmployeeList: React.FC = () => {
    const [employee, setEmployee] = useState<Employee[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchResult, setSearchResult] = useState<string>("");
    const [pageSize, setPageSize] = useState<number>(5);
    const [totalElements, setTotalElement] = useState<number>(0);
    const [selectedIds, setSelectedIds] = useState<number[]>([]);
    const [selectAll, setSelectAll] = useState<boolean>(false);
    const [show, setShow] = useState<boolean>(false);
    const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
    const [showEmployeeModal, setShowEmployeeModal] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [fullAddress, setFullAddress] = useState<{ [key: number]: boolean }>({});
    const [showAlertModal, setShowAlertModal] = useState<boolean>(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [shouldRefresh, setShouldRefresh] = useState<boolean>(false);
    const debouncedSearchTerm = useDebounce(searchResult, 700);
    const token = localStorage.getItem("token");
    const nav = useNavigate();
    document.title = "List Employee";


    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/employee`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            params: {
                pageNo: pageNo,
                pageSize: pageSize,
                sortBy: 'id',
                sortDir: 'desc',
                nameOrId: debouncedSearchTerm
            },
        })
            .then(response => {
                const result: Search<Employee> = response.data;
                setEmployee(result.content);
                setTotalPages(result.totalPaged);
                setTotalElement(result.totalElements);
                setShouldRefresh(false);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            })
            .finally(() => {
                setIsLoading(false);
            })
    }, [pageNo, debouncedSearchTerm, pageSize, token, nav, shouldRefresh]);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setPageSize(parseInt(e.target.value));
        setPageNo(0); // Reset to the first page when page size changes
    };

    const handleCheckboxChange = (employeeId: number) => {
        setSelectedIds(prevSelectedIds => {
            if (prevSelectedIds.includes(employeeId)) {
                return prevSelectedIds.filter(id => id !== employeeId);
            } else {
                return [...prevSelectedIds, employeeId];
            }

        });
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchResult(e.target.value);
        setPageNo(0); // Reset to the first page when search query changes
    };

    const handleSelectAllChange = () => {
        if (selectAll) {
            setSelectedIds([]);
        } else {
            setIsLoading(true);
            axios.get(`${BASE_URL}/vaccination/employee/getAllId`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    nameOrId: searchResult
                }
            })
                .then(response => {
                    const result = response.data;
                    setSelectedIds(result);
                })
                .catch(error => {
                    if (error.response) {
                        showErrorAlert(() => {
                            nav("/");
                        }, error.response.data.message)
                    }
                })
                .finally(() => {
                    setIsLoading(false);
                })
        }
        setSelectAll(!selectAll);
    };

    const handleDeleteSelected = () => {
        setIsLoading(true);
        axios.delete(`${BASE_URL}/vaccination/employee`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            data: selectedIds
        })
            .then(response => {
                setSelectedIds([]);
                setSelectAll(false);
                handleClose();
                toast.success(response.data, {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    progress: undefined,
                    theme: "light",
                    transition: Bounce,
                });
                setShouldRefresh(true);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        setShouldRefresh(true);
                        handleClose();
                    }, error.response.data.message)
                }
            })
            .finally(() => {
                setIsLoading(false);
            })
    };

    const handleEmployeeClick = (employee: Employee) => {
        setSelectedEmployee(employee);
        setShowEmployeeModal(true);
    };

    const handleEmployeeModalClose = () => {
        setShowEmployeeModal(false);
        setSelectedEmployee(null);
    };

    const toggleFullAddress = (employeeId: number) => {
        setFullAddress(prevState => ({
            ...prevState,
            [employeeId]: !prevState[employeeId]
        }));
    };

    const handleUpdateClick = () => {
        if (selectedIds.length === 0) {
            setAlertMessage("No employee selected for update.");
            setShowAlertModal(true);
        } else if (selectedIds.length > 1) {
            setAlertMessage("Please select only one employee for update.");
            setShowAlertModal(true);
        } else {
            nav(`/employee/update/${selectedIds}`);
        }
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>EMPLOYEE LIST</h5>
            {selectedIds.length === 0 ?
                <AlertModal show={show} handleClose={handleClose} message='No data deleted!' />
                :
                <ConfirmModal show={show} handleClose={handleClose}
                    message='Are you sure to delete?' handleLogic={handleDeleteSelected} />
            }
            {showAlertModal && (
                <AlertModal
                    show={showAlertModal}
                    handleClose={() => setShowAlertModal(false)}
                    message={alertMessage}
                />
            )}
            <Container style={{ backgroundColor: "white" }}>
                <Row className='py-3'>
                    <Col style={{ display: 'flex' }}>
                        Show
                        <div style={{ display: 'inline-block', width: 'auto', margin: '0 10px' }}>
                            <select
                                className="custom-select"
                                onChange={handlePageSizeChange}
                                value={pageSize}
                                style={{ display: 'inline-block', width: 'auto', margin: '0 10px' }}
                            >
                                <option value="5">5</option>
                                <option value="10">10</option>
                                <option value="25">25</option>
                                <option value="50">50</option>
                                <option value="100">100</option>
                            </select>
                        </div>
                        entries
                    </Col>
                    <Col style={{ textAlign: 'end' }}>Search: <input className='custom-search' type='text' onChange={handleSearchChange} /></Col>
                </Row>
                <Container as={Row} className='py-3'>
                    {employee.length > 0 ?
                        <div className={pageSize !== 5 ? 'tableFixHead' : ''}>
                            <table>
                                <thead>
                                    <tr>
                                        <th><input type='checkbox' checked={selectAll} onChange={handleSelectAllChange} /></th>
                                        <th>Employee ID</th>
                                        <th>Employee Name</th>
                                        <th>Date of Birth</th>
                                        <th>Gender</th>
                                        <th>Phone</th>
                                        <th style={{ width: '350px' }}>Address</th>
                                        <th>Image</th>
                                    </tr>
                                </thead>

                                <tbody className='text-start'>
                                    {employee.map(e => (
                                        <tr key={e.employeeId}>
                                            <td className='text-center'><input type='checkbox' checked={selectedIds.includes(e.employeeId)} onChange={() => handleCheckboxChange(e.employeeId)} /></td>
                                            <td>
                                                <div className='custom-id' onClick={() => handleEmployeeClick(e)}>
                                                    {e.employeeId}
                                                </div>
                                            </td>
                                            <td>{e.employeeName}</td>
                                            <td>{e.dateOfBirth}</td>
                                            <td>{e.gender}</td>
                                            <td>{e.phone}</td>
                                            <td style={{ width: '350px' }}>
                                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                    {fullAddress[e.employeeId] ? e.address : (e.address.length > 30 ? `${e.address.substring(0, 30)}...` : e.address)}
                                                    {e.address.length > 30 ? <i
                                                        className="fa fa-eye"
                                                        style={{ cursor: 'pointer' }}
                                                        onClick={() => toggleFullAddress(e.employeeId)}
                                                        title={fullAddress[e.employeeId] ? 'Hide address' : 'Show full address'}
                                                    ></i> : null}
                                                </div>
                                            </td>
                                            <td><img src={e?.image ? e?.image : "/assets/manager.png"} alt='' style={{ width: "40px", height: "40px" }}></img></td>
                                        </tr>
                                    ))}


                                </tbody>
                            </table>
                        </div>
                        : <h3>No data found!</h3>}
                </Container>
                <Row className='py-3'>
                    <Col style={{ textAlign: 'start' }}>Showing {pageNo * pageSize + 1} to {(pageNo + 1) * pageSize} of {totalElements} entries</Col>
                    <Col style={{ textAlign: 'end' }}>
                        <Paging currentPage={pageNo} totalPages={totalPages} onPageChange={handlePageChange} />
                    </Col>
                </Row>
                <Row>
                    <Col style={{ textAlign: 'start' }} className='py-2'>
                        <Button className='m-1 rounded-0' style={{ backgroundColor: '#009688', borderColor: '#009688' }}
                            onClick={() => nav(`/employee/create`)}>
                            New Employee
                        </Button>
                        <Button className='m-1 rounded-0 text-white' variant='warning' onClick={handleUpdateClick}>
                            Update Employee
                        </Button>
                        <Button className='m-1 rounded-0' variant='danger' onClick={handleShow}>Delete Employee</Button>
                    </Col>
                </Row>
            </Container>
            {selectedEmployee && (
                <EmployeeDetailModal show={showEmployeeModal} handleClose={handleEmployeeModalClose}
                    employee={selectedEmployee} />
            )}
        </Container>
    )
}

export default EmployeeList