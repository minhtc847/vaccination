import React, { useState, useEffect } from 'react'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import { useDebounce } from "@uidotdev/usehooks";
import { InjectionResult } from '../../Interface/BusinessObjectInterface';
import Paging from '../../Utils/Paging';
import { showErrorAlert } from '../../Utils/ErrorAlert';
import { Search } from '../../Interface/UtilsInterface';
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';
import BASE_URL from '../../Api/BaseApi';


const InjectionResultList: React.FC = () => {

    const [injectResult, setInjectResult] = useState<InjectionResult[]>([]);
    const [pageNo, setPageNo] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchResult, setSearchResult] = useState<string>("");
    const [pageSize, setPageSize] = useState<number>(5);
    const [totalElements, setTotalElement] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const debouncedSearchTerm = useDebounce(searchResult, 700);
    const token = localStorage.getItem("token");
    const nav = useNavigate();
    document.title = "List Injection Result";

    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/result`, {
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
        }).then(response => {
            const result: Search<InjectionResult> = response.data;
            setInjectResult(result.content);
            setTotalPages(result.totalPaged);
            setTotalElement(result.totalElements);
        }).catch(error => {
            if (error.response) {
                showErrorAlert(() => {
                    nav("/");
                }, error.response.data.message)
            }
        }).finally(() => {
            setIsLoading(false);
        })
    }, [pageNo, debouncedSearchTerm, pageSize, token, nav]);

    const handlePageChange = (pageNumber: number) => {
        setPageNo(pageNumber - 1);
    };

    const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setPageSize(parseInt(e.target.value));
        setPageNo(0); // Reset to the first page when page size changes
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchResult(e.target.value);
        setPageNo(0); // Reset to the first page when search query changes
    };

    const formatDate = (dateString: string): string => {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // Adding leading zero and getting month in 2-digit format
        const day = ('0' + date.getDate()).slice(-2); // Adding leading zero and getting day in 2-digit format
        return `${day}/${month}/${year}`;
    }

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>INJECTION RESULT LIST</h5>
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
                    {injectResult.length > 0 ?
                        <div className={pageSize !== 5 ? 'tableFixHead' : ''}>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Customer</th>
                                        <th>Vaccine name</th>
                                        <th>Prevention</th>
                                        <th>Injection</th>
                                        <th>Date of inject</th>
                                        <th>Next inject date</th>
                                    </tr>
                                </thead>

                                <tbody className='text-start'>
                                    {injectResult.map(s => (
                                        <tr key={s.id}>
                                            <td>
                                                {s.customer.id}-{s.customer.employeeName}-{formatDate(s.customer.dateOfBirth)}
                                            </td>
                                            <td>{s.vaccine.vaccineTypeName}</td>
                                            <td>{s.vaccine.vaccineName}</td>
                                            <td>1</td>
                                            <td>{formatDate(s.injectionDate)}</td>
                                            <td>{formatDate(s.nextInjectionDate)}</td>
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
                            onClick={() => nav(`/result/create`)}>
                            Create Injection Result
                        </Button>
                    </Col>
                </Row>
            </Container>
        </Container>
    )
}

export default InjectionResultList