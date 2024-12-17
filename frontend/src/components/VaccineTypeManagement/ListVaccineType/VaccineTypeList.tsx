import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import AlertModal from '../../Modals/AlertModal';
import ConfirmModal from '../../Modals/ConfirmModal';
import { VaccineType } from '../../Interface/BusinessObjectInterface';
import { useDebounce } from "@uidotdev/usehooks";
import Paging from '../../Utils/Paging';
import { showErrorAlert } from '../../Utils/ErrorAlert';
import { Bounce, toast } from 'react-toastify';
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';
import { Search } from '../../Interface/UtilsInterface';
import BASE_URL from '../../Api/BaseApi';

const VaccineTypeList: React.FC = () => {
    const [vaccineType, setVaccineType] = useState<VaccineType[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchResult, setSearchResult] = useState<string>("");
    const [pageSize, setPageSize] = useState<number>(5);
    const [totalElements, setTotalElement] = useState<number>(0);
    const [show, setShow] = useState<boolean>(false);
    const [selectedIds, setSelectedIds] = useState<number[]>([]);
    const [activeIds, setActiveIds] = useState<number[]>([]);
    const [selectAll, setSelectAll] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [fullDescription, setFullDescription] = useState<{ [key: number]: boolean }>({});
    const token = localStorage.getItem("token");
    const debouncedSearchTerm = useDebounce(searchResult, 700);
    const nav = useNavigate();
    document.title = "List Vaccine Type";

    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/vaccinetype`, {
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
                const result: Search<VaccineType> = response.data;
                setVaccineType(result.content);
                setTotalPages(result.totalPaged);
                setTotalElement(result.totalElements);

            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.message)
                }
            })
            .finally(() => {
                setIsLoading(false);
            })
    }, [pageNo, debouncedSearchTerm, pageSize, token, nav]);

    useEffect(() => {
        axios.get(`${BASE_URL}/vaccination/vaccinetype/getAllId`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then(response => {
                const result = response.data;
                setActiveIds(result);
            })
            .catch(error => {
                if (error.response) {
                    showErrorAlert(() => {
                        nav("/");
                    }, error.response.message)
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


    const handleCheckboxChange = (vaccineTypeId: number) => {
        setSelectedIds(prevSelectedIds => {
            if (prevSelectedIds.includes(vaccineTypeId)) {
                return prevSelectedIds.filter(id => id !== vaccineTypeId);
            } else {
                return [...prevSelectedIds, vaccineTypeId];
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
            axios.get(`${BASE_URL}/vaccination/vaccinetype/getAllId`, {
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
                        }, error.response.message)
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
        axios.post(`${BASE_URL}/vaccination/vaccinetype/check`, updateData, {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                setActiveIds((prevActiveIds: number[]) => prevActiveIds.filter((id: number) => !selectedIds.includes(id)));
                setVaccineType(prevVaccineType =>
                    prevVaccineType.map(vt =>
                        selectedIds.includes(vt.id) ? { ...vt, status: false } : vt
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
                        nav("/vaccine_type/list");
                    }, error.response.data.message)
                }
            })
            .finally(() => {
                setIsLoading(false);
            })
    };

    const toggleFullDescription = (desId: number) => {
        setFullDescription(prevState => ({
            ...prevState,
            [desId]: !prevState[desId]
        }));
    };

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>VACCINE TYPE LIST</h5>
            {selectedIds.length === 0 && (
                <AlertModal show={show} handleClose={handleClose} message='No data to make inactive!' />
            )}
            {selectedIds.length > 0 && selectedIds.every(id => activeIds.includes(id)) && (
                <ConfirmModal show={show} handleClose={handleClose}
                    message='Are you sure to make inactive?' handleLogic={handleUpdateSelected} />
            )}
            {selectedIds.length > 0 && !selectedIds.every(id => activeIds.includes(id)) && (
                <AlertModal show={show} handleClose={handleClose}
                    message='Invalid data! Some selected items are already inactive.' />
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
                    {vaccineType.length > 0 ?
                        <div className={pageSize !== 5 ? 'tableFixHead' : ''}>
                            <table>
                                <thead>
                                    <tr>
                                        <th><input type='checkbox' checked={selectAll} onChange={handleSelectAllChange} /></th>
                                        <th style={{ width: "150px" }}>Code</th>
                                        <th style={{ width: "200px" }}>Vaccine Type Name</th>
                                        <th>Description</th>
                                        <th style={{ width: "150px" }}>Status</th>
                                    </tr>
                                </thead>
                                <tbody className='text-start'>
                                    {vaccineType.map(vt => (
                                        <tr key={vt.id}>
                                            <td className='text-center'><input type='checkbox'
                                                checked={selectedIds.includes(vt.id)}
                                                onChange={() => handleCheckboxChange(vt.id)} /></td>
                                            <td style={{ width: "150px" }}>
                                                <Link to={`/vaccine_type/update/${vt.id}`} className='custom-id'>
                                                    {vt.code}
                                                </Link>
                                            </td>
                                            <td style={{ width: "200px" }}>{vt.vaccineTypeName}</td>
                                            <td>
                                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                    {fullDescription[vt.id] ? vt.description : (vt.description.length > 50 ? `${vt.description.substring(0, 50)}...` : vt.description)}
                                                    {vt.description.length > 50 ? <i
                                                        className="fa fa-eye"
                                                        style={{ cursor: 'pointer' }}
                                                        onClick={() => toggleFullDescription(vt.id)}
                                                        title={fullDescription[vt.id] ? 'Hide description' : 'Show full description'}
                                                    ></i> : null}
                                                </div>
                                            </td>
                                            <td style={{ width: "150px" }}>{vt.status ? "Active" : "In-Active"}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        :
                        <h3>No data found!</h3>
                    }
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
                            onClick={() => nav(`/vaccine_type/create`)}>
                            New Vaccine Type
                        </Button>
                        <Button className='m-1 rounded-0' variant='warning' style={{ color: 'white' }} onClick={handleShow}>Make In-active</Button>
                    </Col>
                </Row>
            </Container>
        </Container>
    )
}
export default VaccineTypeList