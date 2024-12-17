import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import AlertModal from '../../Modals/AlertModal';
import ConfirmModal from '../../Modals/ConfirmModal';
import { Vaccine } from '../../Interface/BusinessObjectInterface';
import { useDebounce } from "@uidotdev/usehooks";
import { Bounce, toast } from 'react-toastify';
import Paging from '../../Utils/Paging';
import { showErrorAlert } from '../../Utils/ErrorAlert';
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';
import { Search } from '../../Interface/UtilsInterface';
import BASE_URL from '../../Api/BaseApi';

const VaccineList: React.FC = () => {
    const [vaccine, setVaccine] = useState<Vaccine[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchResult, setSearchResult] = useState<string>("");
    const [pageSize, setPageSize] = useState<number>(5);
    const [totalElements, setTotalElement] = useState<number>(0);
    const [selectedIds, setSelectedIds] = useState<number[]>([]);
    const [activeIds, setActiveIds] = useState<number[]>([]);
    const [selectAll, setSelectAll] = useState<boolean>(false);
    const [show, setShow] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [showAlertModal, setShowAlertModal] = useState<boolean>(false);
    const [alertMessage, setAlertMessage] = useState<string>("");
    const token = localStorage.getItem("token");
    const debouncedSearchTerm = useDebounce(searchResult, 700);
    const nav = useNavigate();
    document.title = "List Vaccine";

    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/vaccine`, {
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
                const result: Search<Vaccine> = response.data;
                setVaccine(result.content);
                setTotalPages(result.totalPaged);
                setTotalElement(result.totalElements);

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
    }, [pageNo, debouncedSearchTerm, pageSize, token, nav]);

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/vaccine/getAllId`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        })
            .then(response => {
                const result = response.data;
                setActiveIds(result);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.data.message)
                }
            })
    }, [token, nav]);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setPageSize(parseInt(e.target.value));
        setPageNo(0); // Reset to the first page when page size changes
    };

    const handleCheckboxChange = (vaccineId: number) => {
        setSelectedIds(prevSelectedIds => {
            if (prevSelectedIds.includes(vaccineId)) {
                return prevSelectedIds.filter(id => id !== vaccineId);
            } else {
                return [...prevSelectedIds, vaccineId];
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
            axios.get(`${BASE_URL}/vaccination/vaccine/getAllId`, {
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

    const handleUpdateSelected = () => {
        setIsLoading(true);
        const updateData = selectedIds.map(id => ({
            id: id,
            status: false
        }))
        axios.post(`${BASE_URL}/vaccination/vaccine/check`, updateData, {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                setActiveIds((prevActiveIds: number[]) => prevActiveIds.filter((id: number) => !selectedIds.includes(id)));
                setVaccine(prevVaccine =>
                    prevVaccine.map(v =>
                        selectedIds.includes(v.id) ? { ...v, status: false } : v
                    )
                );
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

    const handleUpdateClick = () => {
        if (selectedIds.length === 0) {
            setAlertMessage("No vaccine selected for update.");
            setShowAlertModal(true);
        } else if (selectedIds.length > 1) {
            setAlertMessage("Please select only one vaccine for update.");
            setShowAlertModal(true);
        } else {
            nav(`/vaccine/update/${selectedIds}`);
        }
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>VACCINE LIST</h5>
            {selectedIds.length === 0 && (
                <AlertModal show={show} handleClose={handleClose} message='No data to make inactive!' />
            )}
            {selectedIds.length > 0 && selectedIds.every(id => activeIds.includes(id)) && (
                <ConfirmModal show={show} handleClose={handleClose}
                    message='Are you sure to make inactive?' handleLogic={handleUpdateSelected} />
            )}
            {selectedIds.length > 0 && !selectedIds.every(id => activeIds.includes(id)) && (
                <AlertModal show={show} handleClose={handleClose}
                    message='Invalid data! - please recheck your selects!' />
            )}
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
                    {vaccine.length > 0 ?
                        <div className={pageSize !== 5 ? 'tableFixHead' : ''}>
                            <table>
                                <thead>
                                    <tr>
                                        <th><input type='checkbox' checked={selectAll} onChange={handleSelectAllChange} /></th>
                                        <th>Vaccine ID</th>
                                        <th>Vaccine Name</th>
                                        <th>Vaccine Type</th>
                                        <th style={{ width: '100px' }}>Number of injection</th>
                                        <th>Total Inject</th>
                                        <th>Origin</th>
                                        <th style={{ width: '150px' }}>Status</th>
                                    </tr>
                                </thead>

                                <tbody className='text-start'>
                                    {vaccine.map(v => (
                                        <tr key={v.id}>
                                            <td className='text-center'><input type='checkbox' checked={selectedIds.includes(v.id)} onChange={() => handleCheckboxChange(v.id)} /></td>
                                            <td>
                                                <Link to={`/vaccine/update/${v.id}`} className='custom-id'>
                                                    {v.id}
                                                </Link>
                                            </td>
                                            <td>{v.vaccineName}</td>
                                            <td>{v.vaccineTypeName}</td>
                                            <td style={{ width: '100px' }}>{v.numberOfInjection}</td>
                                            <td>{v.totalInject}</td>
                                            <td>{v.origin}</td>
                                            <td style={{ width: '150px' }}>{v.status ? "Active" : "In-Active"}</td>
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
                            onClick={() => nav(`/vaccine/create`)}>
                            New Vaccine
                        </Button>
                        <Button className='m-1 rounded-0 text-white' variant='warning' onClick={handleUpdateClick}>
                            Update Vaccine
                        </Button>
                        <Button className='m-1 rounded-0' variant='danger' onClick={handleShow}>Make In-Active</Button>
                        <Button className='m-1 rounded-0' style={{ backgroundColor: '#009688', borderColor: '#009688' }}
                            onClick={() => nav(`/vaccine/import`)}>
                            Import Vaccine
                        </Button>
                    </Col>
                </Row>
            </Container>
        </Container>
    )
}

export default VaccineList